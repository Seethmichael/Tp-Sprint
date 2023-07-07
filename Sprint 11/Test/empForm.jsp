<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f2f2f2;
        }

        form {
            background-color: #fff;
            border-radius: 5px;
            padding: 20px;
            width: 300px;
            margin: 0 auto;
        }

        p {
            margin-bottom: 10px;
        }

        input[type="text"],
        input[type="date"] {
            width: 100%;
            padding: 5px;
            border-radius: 3px;
            border: 1px solid #ccc;
        }

        input[type="submit"] {
            background-color: #4CAF50;
            color: #fff;
            border: none;
            padding: 10px 20px;
            border-radius: 3px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <form action="showOneEmp">
        <p>Nom: <input type="text" name="nom"></p>
        <p>Age: <input type="text" name="age"></p>
        <p>Date embauche: <input type="date" name="embauche"></p>
        <p>Note: <input type="text" name="note"></p>
        <p>Salaire: <input type="text" name="salaire"></p>
        <input type="submit" value="Valider">
    </form>
</body>
</html>
