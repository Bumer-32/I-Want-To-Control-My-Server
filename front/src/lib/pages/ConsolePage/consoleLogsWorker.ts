self.onmessage = (e: MessageEvent<string>) => {
    const text = e.data;

    const logPattern = /\[(\d{2}:\d{2}:\d{2})] \[(.*?\/\w+)] \((.*?)\) (.*)/;
    const match = text.match(logPattern);

    postMessage([match, text]);
};
