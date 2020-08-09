package machine

import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val coffeeMachine = CoffeeMachine(
            waterVolume = 400,
            milkVolume = 540,
            beansVolume = 120,
            cupCount = 9,
            money = 550)
    do {
        val input = scanner.next()
        coffeeMachine.doAction(input)
    } while (input != "exit")
}

class CoffeeMachine(private var money: Int,
                    private var waterVolume: Int,
                    private var milkVolume: Int,
                    private var beansVolume: Int,
                    private var cupCount: Int) {
    private var state: State

    enum class State {
        CHOOSING_AN_ACTION,
        BUYING_A_DRINK,
        FILLING_UP_WITH_WATER,
        FILLING_UP_WITH_MILK,
        FILLING_UP_WITH_BEANS,
        FILLING_UP_WITH_CUPS
    }

    init {
        state = State.CHOOSING_AN_ACTION
        goToNewState(state)
    }

    private fun goToNewState(state: State) {
        this.state = state
        when (state) {
            State.CHOOSING_AN_ACTION ->
                print("Write action (buy, fill, take, remaining, exit): ")
            State.BUYING_A_DRINK ->
                print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
            State.FILLING_UP_WITH_WATER ->
                print("Write how many ml of water do you want to add: ")
            State.FILLING_UP_WITH_MILK ->
                print("Write how many ml of milk do you want to add: ")
            State.FILLING_UP_WITH_BEANS ->
                print("Write how many grams of coffee beans do you want to add: ")
            State.FILLING_UP_WITH_CUPS ->
                print("Write how many disposable cups of coffee do you want to add: ")
        }

    }

    fun doAction(input: String) {
        when (state) {
            State.CHOOSING_AN_ACTION -> {
                when (input) {
                    "buy" -> {
                        goToNewState(State.BUYING_A_DRINK)
                    }
                    "fill" -> {
                        goToNewState(State.FILLING_UP_WITH_WATER)
                    }
                    "take" -> {
                        take()
                        goToNewState(State.CHOOSING_AN_ACTION)
                    }
                    "remaining" -> {
                        printMachineState()
                        goToNewState(State.CHOOSING_AN_ACTION)
                    }
                }
            }
            State.BUYING_A_DRINK -> {
                if (input != "back") makeDrink(input.toInt())
                goToNewState(State.CHOOSING_AN_ACTION)
            }
            State.FILLING_UP_WITH_WATER -> {
                fill(waterVolume = input.toInt(), milkVolume = 0, beansVolume = 0, cupCount = 0)
                goToNewState(State.FILLING_UP_WITH_MILK)
            }
            State.FILLING_UP_WITH_MILK -> {
                fill(waterVolume = 0, milkVolume = input.toInt(), beansVolume = 0, cupCount = 0)
                goToNewState(State.FILLING_UP_WITH_BEANS)
            }
            State.FILLING_UP_WITH_BEANS -> {
                fill(waterVolume = 0, milkVolume = 0, beansVolume = input.toInt(), cupCount = 0)
                goToNewState(State.FILLING_UP_WITH_CUPS)
            }
            State.FILLING_UP_WITH_CUPS -> {
                fill(waterVolume = 0, milkVolume = 0, beansVolume = 0, cupCount = input.toInt())
                goToNewState(State.CHOOSING_AN_ACTION)
            }

        }
    }

    private fun printMachineState() {
        println("The coffee machine has:")
        println("$waterVolume of water")
        println("$milkVolume of milk")
        println("$beansVolume of coffee beans")
        println("$cupCount of disposable cups")
        println("$money of money")
    }

    private fun makeDrink(choiceNumber: Int) {
        val coffeeDrink = when (choiceNumber) {
            1 -> Espresso()
            2 -> Latte()
            3 -> Cappuccino()
            else -> throw IllegalArgumentException("Wrong choice number")
        }
        val deficientResource = when {
            (this.waterVolume < coffeeDrink.waterVolume) -> "water"
            (this.milkVolume < coffeeDrink.milkVolume) -> "milk"
            (this.beansVolume < coffeeDrink.beansVolume) -> "beans"
            (this.cupCount < 1) -> "cup"
            else -> ""
        }
        if (deficientResource.isEmpty()) {
            cupCount -= 1
            this.waterVolume -= coffeeDrink.waterVolume
            this.milkVolume -= coffeeDrink.milkVolume
            this.beansVolume -= coffeeDrink.beansVolume
            this.money += coffeeDrink.price
            println("I have enough resources, making you a coffee!")
        } else {
            println("Sorry, not enough $deficientResource!")
        }
    }

    private fun fill(waterVolume: Int, milkVolume: Int, beansVolume: Int, cupCount: Int) {
        this.waterVolume += waterVolume
        this.milkVolume += milkVolume
        this.beansVolume += beansVolume
        this.cupCount += cupCount
    }

    private fun take() {
        println("I gave you $$money")
        this.money = 0
    }

}

open class CoffeeDrink(val waterVolume: Int, val milkVolume: Int, val beansVolume: Int, val price: Int)

class Espresso() : CoffeeDrink(waterVolume = 250, milkVolume = 0, beansVolume = 16, price = 4)

class Latte() : CoffeeDrink(waterVolume = 350, milkVolume = 75, beansVolume = 20, price = 7)

class Cappuccino() : CoffeeDrink(waterVolume = 200, milkVolume = 100, beansVolume = 12, price = 6)