export function getCookies(): Map<string, string> {
    const cookiesList = document.cookie.split(';');
    const cookies = new Map<string, string>();
    
    cookiesList.forEach(cookie => {
        const [key, value] = cookie.split('=');
        cookies.set(key, value);
    });

    return cookies;
}

export function editCookie(name: string, value: string, lifetime: number): void {
    const expirationDate = new Date();
    expirationDate.setTime(expirationDate.getTime() + (lifetime * 1000));

    const expires = `expires=${expirationDate.toUTCString()}`;
    document.cookie = `${name}=${value}; ${expires}; path=/`;
}