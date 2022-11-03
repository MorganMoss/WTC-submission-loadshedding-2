import {goToLogin, navigateTo, renderPage} from "./common.js";
import {getPerson} from "./person.js";
import {expensesURL} from "./routes.js";

/**
 * This will setup the navbar and the element for content.
 * It redirects to login if the id or email is null.
 */
export function main() {
    if (!goToLogin()){
        const email = getPerson().email
        const data = {email, name: email[0].toUpperCase() + email.substring(1, email.indexOf("@"))};
        const templateId = "main-template";
        const elementId = "app";

        renderPage(data, templateId, elementId);

        navigateTo(expensesURL);
    }
}
