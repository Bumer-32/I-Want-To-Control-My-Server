import { hocon } from "hocon-web";

export default async function readConfig(selfConfigSetting: AvailableConfigSetting, strategy: Strategy, configFile: string): Promise<Strategy | null> {
    const comparator = ConfigReaders.readers[selfConfigSetting.config_type];
    if (comparator == undefined) return null;
    return await comparator(strategy, configFile);
}

interface StrategyConfig {
    type: string;
    default: string | number | boolean | null;
    min: number;
    max: number;
    step: number;
    options: Array<string>;
}

export type Strategy = Record<string, StrategyConfig>;

export interface AvailableConfigSetting {
    selector_name: string;
    config_name: string;
    config_type: string;
    config_path: string;
    make_backup: boolean;
    read_permission_name: string;
    edit_permission_name: string;
}

export type AvailableConfigs = Record<string, AvailableConfigSetting>;

class ConfigReaders {
    static readers: Record<string, (strategy: Strategy, configFile: string) => Promise<Strategy | null>> = {};

    private static register(name: string, comparator: (strategy: Strategy, configFile: string) => Promise<Strategy | null>) {
        if (this.readers[name]) {
            throw new Error(`Config reader ${name} already registered`);
        }
        this.readers[name] = comparator;
    }

    static {
        this.register("hocon", async (strategy: Strategy, configFile: string) => {
            try {
                const hoconInstance = await hocon();
                const cfg = new hoconInstance.Config(configFile);
                const json = JSON.parse(cfg.toJSON());
                cfg.delete();

                const result: Strategy = {};

                Object.entries(strategy).forEach(([key, value]) => {
                    // ? first try to get the value from the config file
                    const configValue = key.split(".").reduce((o, i) => o[i], json);

                    if (configValue === undefined) {
                        // ? if not found, use the default value from the strategy
                        result[key] = {
                            type: value.type,
                            default: value.default,
                            min: value.min,
                            max: value.max,
                            step: value.step,
                            options: value.options,
                        };
                    } else {
                        // ? if found, use the value from the config file
                        result[key] = {
                            type: value.type,
                            default: configValue,
                            min: value.min,
                            max: value.max,
                            step: value.step,
                            options: value.options,
                        };
                    }
                });

                return result;
            } catch (e) {
                console.error(e);
                return null;
            }
        });

        this.register("minecraft", async (strategy: Strategy, configFile: string) => {
            try {
                const lines: string[] = configFile.split("\r\n");
                const cfg: Record<string, string> = {};
                lines.forEach((line) => {
                    const [key, value] = line.split("=");
                    if (key.startsWith("#") || key.trim().length === 0) return; // ignore comments and empty lines
                    cfg[key] = value;
                });

                const result: Strategy = {};

                Object.entries(strategy).forEach(([key, value]) => {
                    // ? first try to get the value from the config file
                    const configValue = cfg[key];

                    if (configValue === undefined) {
                        // ? if not found, use the default value from the strategy
                        result[key] = {
                            type: value.type,
                            default: value.default,
                            min: value.min,
                            max: value.max,
                            step: value.step,
                            options: value.options,
                        };
                    } else {
                        // ? if found, use the value from the config file
                        result[key] = {
                            type: value.type,
                            default: configValue,
                            min: value.min,
                            max: value.max,
                            step: value.step,
                            options: value.options,
                        };
                    }
                });

                return result;
            } catch (e) {
                console.error(e);
                return null;
            }
        });

        // TODO: add more config readers (json, yaml, etc.)
    }
}

export class ConfigWriters {
    static writers: Record<string, (input: Record<string, any>) => string> = {};

    private static register(name: string, writer: (input: Record<string, any>) => string) {
        if (this.writers[name]) {
            throw new Error(`Config reader ${name} already registered`);
        }
        this.writers[name] = writer;
    }

    public static writeConfig(type: string, input: Record<string, any>): string | null {
        const func = this.writers[type];
        if (func == undefined) return null;
        return func(input);
    }

    static {
        this.register("hocon", (input: Record<string, any>) => {
            function pathToObject(path: string, value: any): any {
                const parts = path.split(".");
                return parts.reduceRight((acc, key) => ({ [key]: acc }), value);
            }

            function deepMerge(target: any, source: any): any {
                for (const key in source) {
                    if (
                        key in target &&
                        typeof target[key] === "object" &&
                        typeof source[key] === "object" &&
                        target[key] !== null &&
                        source[key] !== null
                    ) {
                        deepMerge(target[key], source[key]);
                    } else {
                        target[key] = source[key];
                    }
                }
                return target;
            }

            function toHocon(obj: any, indent = ""): string {
                return Object.entries(obj).map(([key, val]) => {
                    if (typeof val === "object" && val !== null) {
                        return `${indent}${key} {\n${toHocon(val, indent + "  ")}\n${indent}}\n`;
                    } else if (typeof val === "string") {
                        return `${indent}${key} = "${val}"`;
                    } else {
                        return `${indent}${key} = ${val}`;
                    }
                }).join("\n");
            }

            let merged: any = {};
            for (const [key, value] of Object.entries(input)) {
                const obj = pathToObject(key, value);
                merged = deepMerge(merged, obj);
            }

            return toHocon(merged);
        });

        this.register("minecraft", (input: Record<string, any>) =>  {
            let str = ""

            for (const [name, value] of Object.entries(input)) {
                str += `${name}=${value}\n`;
            }

            return str
        });

        // TODO: add more config readers (json, yaml, etc.)
    }
}