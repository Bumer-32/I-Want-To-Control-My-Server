export namespace PlayerData {
    export interface PlayerData {
        pos: Pos;
        username: string;
        uuid: string;
        gameMode: string;
        permissionLevel: PermissionLevel;
        inventory: Slot[];
    }

    export interface Pos {
        x: number;
        y: number;
        z: number;
    }

    export interface Slot {
        slotId: string;
        count: number;
    }

    // it actually used
    // noinspection JSUnusedGlobalSymbols
    export enum PermissionLevel {
        Player = 0,
        Moderator = 1,
        GameMaster = 2,
        Admin = 3,
        Owner = 4,
    }
}
