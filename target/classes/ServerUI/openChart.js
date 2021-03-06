function splitKeywords(keywords, wrongwords) {
    var urlParams = new URLSearchParams(window.location.search);
    for (var pair of urlParams.entries()) {
        if (pair[0].startsWith("key_")) keywords.set(pair[0].substring(4), pair[1]);
        else if (pair[0].startsWith("wrong_")) wrongwords.set(pair[0].substring(6), pair[1])
    }
}

function drawChart(id, mapWords) {
    var ctx = document.getElementById(id).getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Array.from(mapWords.keys()),
            datasets: [{
                label: id === 'myChart1' ? 'Keywords' : 'Common answers that are not in keywords',
                data: Array.from(mapWords.values()),
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                ],
                borderColor: [
                    'rgba(255,99,132,1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                ],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero:true
                    }
                }]
            }
        }
    });
} 

var keywords = new Map();
var wrongwords = new Map();
splitKeywords(keywords, wrongwords);
drawChart("myChart1", keywords);
drawChart("myChart2", wrongwords);
