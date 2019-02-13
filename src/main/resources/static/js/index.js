var bizLine = '';
var appName = '';

var HOST = 'http://10.104.109.231:8412'; //测试环境
// var HOST = 'http://127.0.0.1:8412'; // 本地环境

var getLog;


$(document).ready(function () {
    $("#gen-id").on('click', function () {
        var bizL = $('#bizLine').val();
        var artifactId = $('#appName').val();
        console.log(bizL + '-----' + artifactId);
        if ($.trim(bizL) === '' || $.trim(artifactId) === '') {
            alert("业务线, 应用名称均不可为空");
        } else {
            var reg = /^[a-zA-Z]+[a-zA-Z0-9_]*[a-zA-Z0-9]+$/
            if (!reg.test(bizL) || !reg.test(artifactId)) {
                alert("业务线, 应用名称仅可由字母,下划线，数字组成，且必须以字母开头, 不少于3个字符");
            } else {
                var url = HOST + "/api/gen/gener?bizLine=" + bizL + "&artifactId=" + artifactId;
                document.getElementById("gen-id").setAttribute("disabled", true);
                $.get(url, function (res) {
                    bizLine = bizL;
                    appName = artifactId;
                    getLog = setInterval(appendLog, 1000);
                })
            }

        }
    });
});

/**
 * 获取模板下载链接
 * @param bizLine 业务线
 * @param appName 应用名称
 */
function getDownLink() {
    var bizL = bizLine;
    var artifactId = appName;
    var downUrl = HOST + "/api/gen/down?bizLine=" + bizL + "&artifactId=" + artifactId;
    var fileUrl = HOST + "/api/gen/get?bizLine=" + bizL + "&artifactId=" + artifactId;
    if (bizL === '' || artifactId === '') {
    } else {
        $.get(fileUrl, function (data) {
            $('#fileAppender').append('<p>模板压缩包名称: <b>' + data.name + '</b>, 压缩包大小: <b>' + data.size + ' byte</b>.</p><a href="' + downUrl + '">点击此处下载模板</a>')
        });
    }
}

/**
 * 获取日志
 */
function appendLog() {
    var logUrl = HOST + "/api/gen/logList?bizLine=" + bizLine + "&artifactId=" + appName;
    if (bizLine === '' || appName === '') {
    } else {
        $.get(logUrl, function (data) {
            if (undefined !== data.logList && data.logList.length !== 0) {
                for (var i = 0; i < data.logList.length; i++) {
                    $("#logAppender").append(data.logList[i] + '<br/>');
                    if(data.logList[i].indexOf("SUCCESS") !== -1) {
                        console.log(data.logList[i]);
                        getDownLink();
                        document.getElementById("gen-id").setAttribute("disabled", false);
                    }
                }
            }
            console.log(data)
        });
    }
}