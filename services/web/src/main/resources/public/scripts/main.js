import {handleResponse} from "./common.js";

const options = {
    method: 'GET'
}

function fillTable(data) {
    const schedule = document.getElementById("content");
    // create elements <table> and a <tbody>
    let tbl = document.createElement("table");
    let tblBody = document.createElement("tbody");

    $.each(data["days"], function (i, object) {

        let row = document.createElement("tr");

        $.each(object["slots"], function (j, startEnd) {
            let cell = document.createElement("td");
            let cellText = document.createTextNode(
                "Slot " + j + ":\n"
                + startEnd["start"][0] + ":" + startEnd["start"][1]
                + "-" + startEnd["end"][0] + ":" + startEnd["end"][1]
            );

            cell.appendChild(cellText);
            row.appendChild(cell);
        })

        tblBody.appendChild(row);

    })
    // append the <tbody> inside the <table>
    tbl.appendChild(tblBody);
    // put <table> in the <body>
    schedule.appendChild(tbl);
    // tbl border attribute to
    tbl.setAttribute("border", "2");
}

function getSchedule(province, place, stage = 0){



    fetch('/schedule/' + province + '/' + place + "/" + stage , options )
        .then(handleResponse)
        .then(
            data => {
                console.log(data)
                fillTable(data);
            }
        )
}

function fillPlaces(municipality) {
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

                const province_select = document.getElementById("provinces")
                const place_select = document.getElementById("places");
                place_select.addEventListener('change',
                    event => getSchedule(
                        province_select.options[province_select.selectedIndex].value,
                        event.target.value
                    )
                )
            }, any => {}
        )

    return null;
}

function fillMunicipalities(province) {
    fetch('/municipalities/' + province, options)
        .then(handleResponse)
        .then(
            data => {
                const dropdown = $('#municipalities');
                dropdown.empty();

                dropdown.append('<option selected="true" disabled>Choose Municipality</option>');
                dropdown.prop('selectedIndex', 0);

                $.each(data, function (i, option) {
                    dropdown.append(
                        $('<option/>')
                            .attr("value", option.name)
                            .text(option.name)
                    );
                });

                const municipalities_select = document.getElementById('municipalities');
                municipalities_select.addEventListener(
                    'change', event => fillPlaces(event.target.value)
                );

            }, any => {}
            )
    return null;
}

/**
 * This will setup the navbar and the element for content.
 * It redirects to login if the id or email is null.
 */
export function main() {
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
        province_select.addEventListener(
            'change', event => fillMunicipalities(event.target.value)
        );

    });
}

