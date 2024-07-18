<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Check Balance</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .balance-container {
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            border-radius: 10px;
            text-align: center;
        }
        .balance-container h1 {
            color: #333;
            margin-bottom: 20px;
        }
        .balance-container p {
            font-size: 1.5rem;
            margin-bottom: 20px;
        }
        .balance-container a {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: #fff;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        .balance-container a:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="balance-container">
        <h1>Current Balance</h1>
        <p>Your current balance is: ${currentBalance}</p>
        <a href="customer_dashboard.html">Go to Dashboard</a>
    </div>
</body>
</html>
