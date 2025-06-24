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

    static register(name: string, compatator: (strategy: Strategy, configFile: string) => Promise<Strategy | null>) {
        if (this.readers[name]) {
            throw new Error(`Config reader ${name} already registered`);
        }
        this.readers[name] = compatator;
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
