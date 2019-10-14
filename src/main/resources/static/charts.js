
function loadMap(velocityMS, climbingDescendingRate, mph, plotName, y_legend_name) {

    var chart = new CanvasJS.Chart("chartContainer", {
        animationEnabled: true,
        theme: "light2",
        title:{
            text: plotName
        },
        axisX:{
            valueFormatString: "",
            crosshair: {
                enabled: true,
                snapToDataPoint: true
            }
        },
        axisY: {
            title: y_legend_name,
            crosshair: {
                enabled: true
            }
        },
        toolTip:{
            shared:true
        },
        legend:{
            cursor:"pointer",
            verticalAlign: "bottom",
            horizontalAlign: "left",
            dockInsidePlotArea: true,
            itemclick: toogleDataSeries
        },
        data: [{
            type: "line",
            showInLegend: true,
            name: "Velocity m/s",
            markerType: "dash",
            color: "#F08010",
            dataPoints: velocityMS
        },
            {
                type: "line",
                showInLegend: true,
                name: "Climbing / Descends",
                markerType: "dot",
                color: "#1FF91F",
                dataPoints: climbingDescendingRate
            },
            {
                type: "line",
                showInLegend: true,
                name: "Miles per hour",
                markerType: "dash",
                color: "#FF001F",
                dataPoints: mph
            }]
    });
    chart.render();

    function toogleDataSeries(e){
        if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
            e.dataSeries.visible = false;
        } else{
            e.dataSeries.visible = true;
        }
        chart.render();
    }

}
