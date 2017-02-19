(function(window, document, $){

    'use strict';

    var SEARCH_ENDPOINT = 'http://ec2-52-67-209-254.sa-east-1.compute.amazonaws.com:8080?query=';
    var TICKET_ENDPOINT = 'http://ec2-52-67-247-192.sa-east-1.compute.amazonaws.com:8080/';
    var timeoutRef;

    function onResultSuccess(data) {
        clearTimeout(timeoutRef);
        $('#result-body').html(null);

        console.log(data);

        var matches = data.matches;
        if (matches == null) {
            $('#load').css('display', 'none');                
            $('#notfound').css('display', 'block');
            return;
        }



        for (var index in matches) {
            var match = matches[index];
            var file  = match.file;
            var score = match.score;
            var link  = match.url;

            var tr = '<tr>';
            tr += '<th scope="row">'+ index +'</th>';
            tr += '<td>'+ file +'</td>';
            tr += '<td>'+ score +'</td>';
            tr += '<td><a href="'+ link +'" target="_blank">Download</a></td>';
            tr += '</tr>';            

            $('#result-body').append(tr);
        }

        $('#notfound').css('display', 'none');
        $('#load').css('display', 'none');                
        $('#result').css('display', 'block');            
    }

    function onResultError(e) {
        console.log(e);
    }

    function onSearchSuccess(data) {
        var ticket = data.ticket;
        var url = TICKET_ENDPOINT + ticket;

        timeoutRef = setTimeout(function(){ onSearchSuccess(data); }, 1000);

        $.ajax({
            url: url,
            method: 'GET',
            success: onResultSuccess,
            error: onResultError,
            complete: timeoutRef,
            timeout: 2000
        });
    }

    function onSearchError(e) {
        console.log(e);
    }

    function onSearchClick(evt) {        
        var query = $('#search-content').val();
        if (query == null)
            return;

        $('#notfound').css('display', 'none');    
        $('#result').css('display', 'none');    
        $('#load').css('display', 'block');

        var url = SEARCH_ENDPOINT + encodeURIComponent(query);     
        $.ajax({
            url: url,
            method: 'GET',
            success: onSearchSuccess,
            error: onSearchError
        });    
    }



    function initView() {
        $('#search').click(onSearchClick)
    }


    $(document).ready(initView);

})(window, document, jQuery);