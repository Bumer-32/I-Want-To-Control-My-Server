export default function slideOnOverflow(element: HTMLElement) {
    const io = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting) {
            if (element.scrollWidth > element.clientWidth) {
                element.classList.add("slide-on-overflow");
                element.style.setProperty("--slide-on-overflow", `-${element.scrollWidth - element.clientWidth}px`);
            }
            io.disconnect();
        }
    });
    io.observe(element);
}
