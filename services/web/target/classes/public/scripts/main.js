import {renderPage} from "./common.js";

/**
 * This will setup the navbar and the element for content.
 * It redirects to login if the id or email is null.
 */
export function main() {
        const data = {}
        const templateId = "main-template";
        const elementId = "app";

        renderPage(data, templateId, elementId);
}
