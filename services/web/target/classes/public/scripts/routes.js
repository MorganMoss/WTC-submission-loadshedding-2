import {main} from "./main.js";

export const mainURL = '';

/**
 * Sets up all the routing for the webapp.
 * This is called when the app starts.
 */
export function setupRoutes(){

    window.addEventListener('load', () => {
        const router = new Router({
            mode:'hash',
            root:'index.html',
            page404: notFound
        });

        router.add(mainURL, main, {});

        router.addUriListener();

        $('a').on('click', (event) => {
            event.preventDefault();
            const target = $(event.target);
            const href = target.attr('href');
            const path = href.substring(href.lastIndexOf('/'));
            router.navigateTo(path);
        });

        router.navigateTo('/');
    });
}

