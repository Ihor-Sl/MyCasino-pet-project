const sock = new SockJS("/roulette")
const client = Stomp.over(sock)

const chatInput = document.getElementById("chatInput")
const chat = document.getElementById("chat")
const timer = document.getElementById("timer")
const balance = document.getElementById("balance")
const gameConsole = document.getElementById("bets")
const roulette = document.getElementById("roulette")

client.connect({}, frame => {

    client.subscribe("/topic/roulette", payload => {
        const body = JSON.parse(payload.body)
        timer.innerText = `Time-left: ${body.timeLeft}`
        if (body.winNumber !== null) {
            spin(body.winNumber)
            setTimeout(() => {
                timer.innerText = `Win number is: ${body.winNumber}!`
            }, 5000)
        }
    })

    client.subscribe("/topic/chat", payload => {
        const message = document.getElementById("chatMessage").cloneNode(true);
        const body = JSON.parse(payload.body)
        message.innerText = `(${body.date}) ${body.from}: ${body.message} `
        const shouldScroll = (chat.scrollTop + chat.clientHeight) === chat.scrollHeight
        chat.appendChild(message)
        if (shouldScroll) {
            chat.scrollTop = chat.scrollHeight
        }
    })

    client.subscribe("/user/topic/console", payload => {
        const message = document.getElementById("betMessage").cloneNode(true);
        const body = JSON.parse(payload.body)
        if (body.betColor && body.amount && body.ownerName) {
            drawBalance(body.currentBalance)
            message.innerText = `You bet ${body.amount}$ on ${body.betColor}`
        }
        if (body.win === true) {
            setTimeout(() => {
                drawBalance(body.currentBalance)
                message.innerText = "Win! :)"
                gameConsole.appendChild(message)
                gameConsole.scrollTop = gameConsole.scrollHeight
            }, 5000)
        }
        if (body.win === false) {
            setTimeout(() => {
                message.innerText = "Lose :("
                gameConsole.appendChild(message)
                gameConsole.scrollTop = gameConsole.scrollHeight
            }, 5000)
        }
        if (body.error) {
            message.innerText = `${body.error}`
        }
        if (message.innerText !== "") {
            gameConsole.appendChild(message)
            gameConsole.scrollTop = gameConsole.scrollHeight
        }
    })

})

function makeBet(color) {
    const input = document.getElementById("amount")
    const amount = Number(input.value)
    client.send("/ws/roulette/bets", {}, JSON.stringify({amount: amount, betColor: color}))
}

function sendMessage(string = false) {
    if (string) {
        chatInput.value = string
    }
    if (chatInput.value === "" || chatInput.value.trim() === "") {
        chatInput.value = ""
        return
    }
    client.send("/ws/roulette/chat", {}, chatInput.value)
    chatInput.value = ""
}

function drawBalance(balanceToDraw) {
    balance.innerText = balanceToDraw
}

function spin(winNumber, isOnlyRead = false) {
    let rotate = 0
    switch (winNumber) {
        case 2:
            rotate = 0
            break
        case 8:
            rotate = 24
            break
        case 1:
            rotate = 48
            break
        case 0:
            rotate = 72
            break
        case 14:
            rotate = 96
            break
        case 7:
            rotate = 120
            break
        case 13:
            rotate = 144
            break
        case 6:
            rotate = 168
            break
        case 12:
            rotate = -168
            break
        case 5:
            rotate = -144
            break
        case 11:
            rotate = -120
            break
        case 4:
            rotate = -96
            break
        case 10:
            rotate = -72
            break
        case 3:
            rotate = -48
            break
        case 9:
            rotate = -24
            break
    }
    if (isOnlyRead) {
        return rotate
    }
    roulette.style = `transform: rotate(${2160 + rotate}deg); transition: all 5s ease;`
    setTimeout(() => {
        roulette.style = ""
        roulette.style = `transform: rotate(${(2160 + rotate) % 360}deg);`
    }, 5000)
}

chatInput.addEventListener("keyup", function (e) {
    if (e.keyCode === 13) {
        sendMessage()
    }
})
