package messageBroker

import io.javalin.Javalin
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue

val subscriptions = ConcurrentLinkedQueue<CompletableFuture<String>>()

fun main(args: Array<String>) {


    val queue = ConcurrentLinkedQueue<String>()

    val queue_max = 3
    val HTTP_TOO_BUSY = 503


    val app = Javalin.create().apply {
        port(8888)
        exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
        error(404) { ctx
            -> ctx.json("not found")
        }
    }.start()


    app.get("/hello") { ctx -> ctx.result("Hello World") }


    val runner = Runnable {
        while (true) {
            sleep(1000)
            if (!queue.isEmpty() && !subscriptions.isEmpty()) {
                val item = queue.remove()
                val subscriber = subscriptions.remove()

                subscriber.complete(item)
            }
        }
    }

    Thread(runner).start()

    app.get("/submit") { ctx ->
        val item = ctx.queryParams("item")

        if (queue.size >= queue_max) {
            ctx.status(HTTP_TOO_BUSY)
        }
        else {
            queue.add(item[0])
            ctx.status(202)  // accepted
        }
    }

    app.get("/subscribe") { ctx ->
        ctx.result(getFuture())
    }

// Creates new CompletableFuture and immediately invokes apply on it returning 'this' to permit further chaining
}

fun getFuture() = CompletableFuture<String>().apply {

    subscriptions.add(this)
    //Executors.newSingleThreadScheduledExecutor().schedule({ this.complete("Hello World!") }, 1, TimeUnit.SECONDS)
}

