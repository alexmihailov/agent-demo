<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${name}!</title>
</head>
<body>
<style type="text/css">
    .center {
       position: absolute;
        top: 30%;
        left: 50%;
        margin-top: -50px;
        margin-left: -50px;
    }
</style>
<div class="center">
    <h2>Hello ${name}!</h2>
    <img src="data:image/png;base64,${gravatar}" alt="Hello ${name}!">
</div>
</body>
</html>
