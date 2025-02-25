


document.addEventListener('DOMContentLoaded', function () {

    initializeCurrentPeriods();
    updatePeriodSelector();


    let chartData = {
        labels: [],
        datasets: []
    };

    let jsonData;
    let listeData = [];

    function getData(periodInfo) {
        let fromDate = periodInfo.start, toDate = periodInfo.end, limit = 1000, dataBaseCode = 1;

        if (periodInfo.isSingleDay == true) {
            dataBaseCode = 0;
        }



        console.log(`api/basicStatements/DateBetween/${fromDate}/${toDate}/${limit}/${dataBaseCode}`);

        fetch(`api/basicStatements/DateBetween/${fromDate}/${toDate}/${limit}/${dataBaseCode}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur de réseau ou réponse non valide');
                }
                return response.json();
            })
            .then(data => {
                if (!Array.isArray(data)) {
                    throw new Error('Les données reçues ne sont pas au format attendu');
                }

                jsonData = data;
                // Réinitialiser les données
                listeData = [];
                chartData.labels = [];
                chartData.datasets = [];

                processJsonData(jsonData, periodInfo.isSingleDay);

                listeData.forEach(item => {
                    if (periodInfo.isSingleDay == true) {
                        sortByHour(item.label.nom);
                    } else {
                        sortByDate(item.label.nom);
                    }
                });

                createGraph(periodInfo.isSingleDay == true);
                myChart.update();
            })
            .catch(error => {
                console.error('Il y a eu un problème avec la requête Fetch :', error);
                alert('Une erreur est survenue lors du chargement des données. Veuillez réessayer plus tard.');
            });
    }

    function processJsonData(jsonData, isSingleDay) {
        console.log("processJsonData" + jsonData);
        jsonData.forEach(data => {
            const date = new Date(data.date);
            if (isSingleDay == true) {
                const hour = date.getHours();
                addToLabel(data.valueName, hour, data.value, true);
            } else {
                const dateStr = date.toISOString().split('T')[0];
                addToLabel(data.valueName, dateStr, data.value, false);
            }
        });
    }

    function pushLabel(name) {
        let newLabel = {
            label: {
                nom: name,
                data: []
            }
        };
        listeData.push(newLabel);
    }

    function addToLabel(labelName, timePoint, value, isHourly) {
        let label = listeData.find(item => item.label.nom === labelName);
        if (!label) {
            pushLabel(labelName);
            label = listeData.find(item => item.label.nom === labelName);
        }

        if (isHourly) {
            // Pour les données horaires
            let existingEntry = label.label.data.find(entry => entry[0] === timePoint);
            if (existingEntry) {
                existingEntry[1] = (existingEntry[1] + value) / 2;
            } else {
                label.label.data.push([timePoint, value]);
            }
        } else {
            // Pour les données journalières
            let existingEntry = label.label.data.find(entry => entry[0] === timePoint);
            if (existingEntry) {
                existingEntry[1] = (existingEntry[1] + value) / 2;
            } else {
                label.label.data.push([timePoint, value]);
            }
        }
    }

    function sortByDate(labelName) {
        let label = listeData.find(item => item.label.nom === labelName);
        if (label) {
            label.label.data.sort((a, b) => {
                let dateA = new Date(a[0]);
                let dateB = new Date(b[0]);
                return dateA - dateB;
            });
        }
    }

    function sortByHour(labelName) {
        let label = listeData.find(item => item.label.nom === labelName);
        if (label) {
            label.label.data.sort((a, b) => a[0] - b[0]);
        }
    }

    function getRandomColor() {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }

    function createGraph(isSingleDay) {
        if (isSingleDay == true) {
            // Configuration pour l'affichage horaire
            chartData.labels = Array.from({ length: 24 }, (_, i) => `${i}h`);

            listeData.forEach(item => {
                const dataset = {
                    label: item.label.nom,
                    data: Array(24).fill(null),
                    borderColor: getRandomColor(),
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    fill: false
                };

                item.label.data.forEach(([hour, value]) => {
                    dataset.data[hour] = value;
                });

                chartData.datasets.push(dataset);
            });
        } else {
            // Configuration pour l'affichage par date
            const allDates = listeData.flatMap(entry => entry.label.data.map(d => d[0]));
            const minDate = new Date(Math.min(...allDates.map(date => new Date(date))));
            const maxDate = new Date(Math.max(...allDates.map(date => new Date(date))));

            const getDateRange = (start, end) => {
                const dates = [];
                let currentDate = new Date(start);
                while (currentDate <= end) {
                    dates.push(currentDate.toISOString().split('T')[0]);
                    currentDate.setDate(currentDate.getDate() + 1);
                }
                return dates;
            };

            const dateRange = getDateRange(minDate, maxDate);
            chartData.labels = dateRange;

            const dataMap = {};
            listeData.forEach(entry => {
                entry.label.data.forEach(([date, value]) => {
                    if (!dataMap[date]) dataMap[date] = {};
                    dataMap[date][entry.label.nom] = value;
                });
            });

            listeData.forEach(item => {
                const dataset = {
                    label: item.label.nom,
                    data: chartData.labels.map(date => dataMap[date]?.[item.label.nom] ?? null),
                    borderColor: getRandomColor(),
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    fill: false
                };
                chartData.datasets.push(dataset);
            });
        }
    }
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________


    function updatePeriodSelector() {
        // Masquer tous les sélecteurs de période
        document.querySelectorAll('.period-selector').forEach(selector => {
            selector.style.display = 'none';
        });

        // Afficher le sélecteur approprié en fonction du type de période choisi
        const periodType = document.getElementById('periodTypeSelector').value;
        document.getElementById(periodType + 'Selector').style.display = 'block';
    }

    function getPeriodDates() {
        const periodType = document.getElementById('periodTypeSelector').value;
        let startDate, endDate, isSingleDay;

        switch (periodType) {
            case 'day':
                const selectedDay = document.getElementById('specificDay').value;
                startDate = new Date(selectedDay);
                endDate = new Date(selectedDay);
                isSingleDay = true;
                break;

            case 'week':
                const selectedWeek = document.getElementById('specificWeek').value;
                // Format: 2025-W08
                const weekYear = selectedWeek.split('-')[0];
                const weekNum = selectedWeek.split('-W')[1];

                // Calculer la date du premier jour de la semaine (lundi)
                startDate = getFirstDayOfWeek(weekYear, weekNum);

                // La fin de la semaine est 6 jours plus tard (dimanche)
                endDate = new Date(startDate);
                endDate.setDate(startDate.getDate() + 6);
                isSingleDay = false;
                break;

            case 'month':
                const selectedMonth = document.getElementById('specificMonth').value;
                // Format: 2025-02
                const [monthYear, monthNum] = selectedMonth.split('-');

                // Premier jour du mois
                startDate = new Date(monthYear, parseInt(monthNum) - 1, 1);

                // Dernier jour du mois
                endDate = new Date(monthYear, parseInt(monthNum), 0);
                isSingleDay = false;
                break;

            case 'year':
                const selectedYear = document.getElementById('specificYear').value;

                // Premier jour de l'année
                startDate = new Date(selectedYear, 0, 1);

                // Dernier jour de l'année
                endDate = new Date(selectedYear, 11, 31);
                isSingleDay = false;
                break;
        }

        // Formater les dates en YYYY-MM-DD
        const formatDate = (date) => {
            return date.toISOString().split('T')[0];
        };

        // Objet contenant les informations de la période
        const periodInfo = {
            start: formatDate(startDate),
            end: formatDate(endDate),
            isSingleDay: isSingleDay
        };

        console.log("Informations de la période sélectionnée:", periodInfo);
        getData(periodInfo);

        // Ici, vous pouvez appeler votre fonction pour actualiser le graphique
        // actualisationGraphique(periodInfo);

        return periodInfo;
    }

    // Fonction pour calculer le premier jour d'une semaine spécifique
    function getFirstDayOfWeek(year, weekNumber) {
        // Créer une date pour le 1er janvier de l'année spécifiée
        const januaryFirst = new Date(year, 0, 1);

        // Trouver le premier lundi de l'année
        const firstMonday = new Date(januaryFirst);
        const dayOfWeek = januaryFirst.getDay() || 7; // Convertir 0 (dimanche) en 7
        if (dayOfWeek > 1) {
            firstMonday.setDate(januaryFirst.getDate() + (8 - dayOfWeek));
        }

        // Ajouter (weekNumber - 1) semaines au premier lundi
        const result = new Date(firstMonday);
        result.setDate(firstMonday.getDate() + (parseInt(weekNumber) - 1) * 7);

        return result;
    }

    // Initialiser le sélecteur avec la date actuelle
    function initializeCurrentPeriods() {
        const now = new Date();

        // Initialiser le jour (format YYYY-MM-DD)
        const currentDay = now.toISOString().split('T')[0];
        document.getElementById('specificDay').value = currentDay;

        // Initialiser la semaine (format YYYY-Wnn)
        const startOfYear = new Date(now.getFullYear(), 0, 1);
        const days = Math.floor((now - startOfYear) / (24 * 60 * 60 * 1000));
        const weekNumber = Math.ceil((days + startOfYear.getDay() + 1) / 7);
        // Ajouter un zéro devant si nécessaire
        const paddedWeek = weekNumber.toString().padStart(2, '0');
        document.getElementById('specificWeek').value = `${now.getFullYear()}-W${paddedWeek}`;

        // Initialiser le mois (format YYYY-MM)
        const currentMonth = `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}`;
        document.getElementById('specificMonth').value = currentMonth;

        // Initialiser les années (de l'année actuelle à 5 ans en arrière)
        const yearSelect = document.getElementById('specificYear');
        const currentYear = now.getFullYear();
        for (let year = currentYear; year >= currentYear - 5; year--) {
            const option = document.createElement('option');
            option.value = year;
            option.textContent = year;
            yearSelect.appendChild(option);
        }
    }




    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________________

    const refreshBtn = document.getElementById('refreshBtn');
    const periodTypeSelector = document.getElementById('periodTypeSelector');

    // Ajouter un écouteur d'événement pour le clic
    refreshBtn.addEventListener('click', function () {
        // L'action à réaliser au clic du bouton
        getPeriodDates();
    });

    periodTypeSelector.addEventListener('change', function () {
        // L'action à réaliser au clic du bouton
        updatePeriodSelector();
    });



    // Initialisation du graphique
    const ctx = document.getElementById('myChart').getContext('2d');
    const myChart = new Chart(ctx, {
        type: 'line',
        data: chartData,
        options: {
            spanGaps: true,
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Période'
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: 'Valeur'
                    }
                }
            }
        }
    });

    // Ajout des gestionnaires d'événements pour les boutons de période
    document.querySelectorAll('[data-period]').forEach(button => {
        button.addEventListener('click', (e) => {
            const period = e.target.dataset.period;
            getData(period);
        });
    });

    // Chargement initial avec la période par défaut
    getPeriodDates();
});