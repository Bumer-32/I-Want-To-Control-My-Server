export class ToastSystem {
    private static queue: { text: string, type: string }[] = [];
    private static isShowing: boolean = false;
    private static notification: HTMLDivElement = document.querySelector(".toast-notifications") as HTMLDivElement;

    public static enabled = true;

    private static addToQueue(text: string, type: string): void {
        this.queue.push({ text, type });
        this.showNext();
    }

    public static showError(text: string): void {
        this.addToQueue(text, "error");
    }

    public static showWarning(text: string): void {
        this.addToQueue(text, "warning");
    }

    public static showInfo(text: string): void {
        this.addToQueue(text, "info");
    }

    private static async showNext(showTime: number = 5000) {
        if (this.isShowing || this.queue.length === 0) return;
        this.isShowing = true;
        const { text, type } = this.queue.shift()!;
        await this.show(text, type, showTime);
        setTimeout(() => {
            this.isShowing = false;
            this.showNext();
        }, 1600 + showTime);
    }

    private static async show(text: string, type: string, showTime: number) {
        if (!this.enabled) return;

        this.notification.style.color = "transparent";
        this.notification.style.backgroundColor = "transparent";
        this.notification.style.boxShadow = "5px 5px transparent"
        this.notification.innerHTML = text;
        this.notification.style.display = "block";

        setTimeout(() => {
            this.notification.style.color = `var(--toast-notification-${type}-text-color)`;
            this.notification.style.backgroundColor = `var(--toast-notification-${type}-color)`;
            this.notification.style.boxShadow = `5px 5px var(--toast-notification-${type}-shadow-color)`
            setTimeout(() => {
                this.notification.style.color = "transparent";
                this.notification.style.backgroundColor = "transparent";
                this.notification.style.boxShadow = "5px 5px transparent"
                setTimeout(() => {
                    this.notification.innerHTML = "";
                    this.notification.style.display = "none";
                }, 1000);
            }, showTime);
        }, 100);
    }
}