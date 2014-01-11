function refreshImage(elem){
    elem.src = elem.src + '#' + new Date().getTime();
}

var sliderChangeBound = false;

function refresh(){
    $.getJSON("/result", function(data){
        $('.distance').html('');
        $('.azimuth').html('');
        $('.hot').html('');
        for(var i=0; i<data.targets.length; i++){
            var target = data.targets[i];
            var targetElem = $('#target-'+target.side);
            targetElem.find('.distance').html(Math.round(target.distance)+'in');
            targetElem.find('.azimuth').html(Math.round(target.azimuth)+'&deg;');
            targetElem.find('.hot').html(target.hot ? "Hot" : "Not hot");
        }

        var config = data.config;
        $('#hue-slider').slider("values", [config.hueMin, config.hueMax]);
        $('#sat-slider').slider("values", [config.satMin, config.satMax]);
        $('#val-slider').slider("values", [config.valMin, config.valMax]);

        $('img.refreshable').each(function(index, elem){
            refreshImage(elem);
        });

        if(!sliderChangeBound){
            console.log("Change event not bound");
            $('.slider').on('slidestop', function(event, ui){
                var newConfig = {};
                newConfig.hueMin = $('#hue-slider').slider('values', 0);
                newConfig.hueMax = $('#hue-slider').slider('values', 1);
                newConfig.satMin = $('#sat-slider').slider('values', 0);
                newConfig.satMax = $('#sat-slider').slider('values', 1);
                newConfig.valMin = $('#val-slider').slider('values', 0);
                newConfig.valMax = $('#val-slider').slider('values', 1);
                console.log(newConfig);

                $.post('/config', JSON.stringify(newConfig), function(data, status, xhr){
                    refresh();
                });

                return true;
            });
            sliderChangeBound = true;
        }
    });
}

$(function(){
    $('.image-thresh').slider({min: 0, max: 255, range: true});
    refresh();
});
