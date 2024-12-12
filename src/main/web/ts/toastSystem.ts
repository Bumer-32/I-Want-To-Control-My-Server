export class ToastSystem {
    private static queue: { text: string, type: string }[] = [];
    private static isShowing: boolean = false;
    private static notification: HTMLDivElement = document.querySelector(".toast-notifications") as HTMLDivElement;

    public static addToQueue(text: string, type: string): void {
        this.queue.push({ text, type });
        this.showNext();
    }

    private static async showNext() {
        if (this.isShowing || this.queue.length === 0) return;
        this.isShowing = true;
        const { text, type } = this.queue.shift()!;
        await this.show(text, type);
        setTimeout(() => {
            this.isShowing = false;
            this.showNext();
        }, 5000);
    }

    private static async show(text: string, type: string) {
        this.notification.style.color = "transparent";
        this.notification.style.backgroundColor = "transparent";
        this.notification.innerHTML = text;
        this.notification.style.display = "block";

        setTimeout(() => {
            this.notification.style.color = `var(--toast-notification-${type}-text-color)`;
            this.notification.style.backgroundColor = `var(--toast-notification-${type}-color)`;
            setTimeout(() => {
                this.notification.style.color = "transparent";
                this.notification.style.backgroundColor = "transparent";
                setTimeout(() => {
                    this.notification.innerHTML = "";
                    this.notification.style.display = "none";
                }, 4000);
            }, 15000);
        }, 100);
    }
}