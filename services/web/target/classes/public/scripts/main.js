import {handleResponse} from "./common.js";


function fillPlaces(municipality) {
    const options = {
        method: 'GET'
    }
    fetch('/places/municipality/' +municipality, options )
        .then(handleResponse)
        .then(
            data => {
                const dropdown = $('#places');
                dropdown.empty();
                dropdown.append('<option selected="true" disabled>Choose Place</option>');
                dropdown.prop('selectedIndex', 0);

                $.each(data, function (i, option) {
                    console.log(option.name)
                    dropdown.append(
                        $('<option/>')
                            .attr("value", option.name)
                            .text(option.name)
                    );
                });
            }, any => {console.log("HERE YAY")}
        )

    return null;
}

function fillMunicipalities(province) {
    console.log(province);
    const options = {
        method: 'GET'
    }
    fetch('/municipalities/' + province, options)
        .then(handleResponse)
        .then(
            data => {
                const dropdown = $('#municipalities');
                dropdown.empty();

                dropdown.append('<option selected="true" disabled>Choose Municipality</option>');
                dropdown.prop('selectedIndex', 0);

                $.each(data, function (i, option) {
                    console.log(option.name)
                    dropdown.append(
                        $('<option/>')
                            .attr("value", option.name)
                            .text(option.name)
                    );
                });

                const municipalities_select = document.getElementById('municipalities');
                municipalities_select.addEventListener('change', event => fillPlaces(event.target.value));

            }, any => {
                console.log("HERE YAY")}
            )
    return null;
}

/**
 * This will setup the navbar and the element for content.
 * It redirects to login if the id or email is null.
 */
export function main() {
const options = {
    method: 'GET'
}
fetch(`/provinces`, options)
    .then(handleResponse)
    .then(data => {
        $('#places').empty();

        const dropdown = $('#provinces');
        dropdown.empty();

        dropdown.append('<option selected="true" disabled>Choose Province</option>');
        dropdown.prop('selectedIndex', 0);

        $.each(data, function (i, option) {
            console.log(option.name)
            dropdown.append(
                $('<option/>')
                    .attr("value", option.name)
                    .text(option.name)
            );
        });

        const province_select = document.getElementById('provinces');
        province_select.addEventListener('change', event => fillMunicipalities(event.target.value));

    });
}

