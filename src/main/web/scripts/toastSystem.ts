enum ToastType {
    INFO = "info",
    WARNING = "warning",
    ERROR = "error",
}

export default class ToastSystem {
    private static queue: { text: string; type: ToastType }[] = [];
    private static isShowing: boolean = false;
    public static notification: HTMLDivElement; // = document.querySelector<HTMLDivElement>(".toast-notifications")!;

    public static enabled = true;

    static ToastType = ToastType;

    public static addToQueue(text: string, type: ToastType): void {
        this.queue.push({ text, type });
        this.showNext();
    }

    private static showNext(showTime: number = 5000) {
        if (this.isShowing || this.queue.length === 0) return;
        this.isShowing = true;
        const { text, type } = this.queue.shift()!;
        this.show(text, type, showTime);
        setTimeout(() => {
            this.isShowing = false;
            this.showNext();
        }, 1600 + showTime);
    }

    private static show(text: string, type: ToastType, showTime: number) {
        if (!this.enabled) return;

        this.notification.style.color = "transparent";
        this.notification.style.backgroundColor = "transparent";
        this.notification.style.boxShadow = "5px 5px transparent";
        this.notification.innerHTML = text;
        this.notification.style.display = "block";

        setTimeout(() => {
            this.notification.style.color = `var(--toast-notification-${type}-text-color)`;
            this.notification.style.backgroundColor = `var(--toast-notification-${type}-color)`;
            this.notification.style.boxShadow = `5px 5px var(--toast-notification-${type}-shadow-color)`;
            setTimeout(() => {
                this.notification.style.color = "transparent";
                this.notification.style.backgroundColor = "transparent";
                this.notification.style.boxShadow = "5px 5px transparent";
                setTimeout(() => {
                    this.notification.innerHTML = "";
                    this.notification.style.display = "none";
                }, 1000);
            }, showTime);
        }, 100);
    }
}
