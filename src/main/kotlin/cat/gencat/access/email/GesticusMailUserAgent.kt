package cat.gencat.access.email


import cat.gencat.access.functions.Utils.Companion.APP_TITLE
import cat.gencat.access.functions.Utils.Companion.decrypt
import cat.gencat.access.functions.Utils.Companion.writeToLog
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.concurrent.thread


const val USER_NAME = "fpestades@xtec.cat"
const val USER_PASSWORD = "frHnSibyReD6nsqPCuG/i0ezR0y9Uh8N"

const val SECRET_PASSWORD = "secret"

const val PORT_SSL = "465"
const val PORT_TLS = "587"

const val GMAIL_LIMIT_PER_HOUR = 99

/*
*
* First ever email 1971
*
* SSL and TLS both provide a way to encrypt a communication channel between two computers.
*
* TLS is the successor of SSL and both terms are used interchangeably in general.
*
* STARTTLS is a way to tacke an existing insecure connections and upgrade it to a secure
* connection using SSL/TLS.
*
* Version numbering is inconsistent between SSL and TLS. When TLS took over from SSL as the
* preferred protocol name, it began a new version number, and also began using sub-versions.
*
* SSL v2, SSL v3, TLS v1.0, TLS v1.1, TLS v1.2, TLS v.1.3
*
* SMTP (Simple Mail Protocol) uses port 23 and was originally designed for transfer, not submission.
*
* So yet another port (587) was defined for message submission.
*
* In most cases, systems that offer message submission over port 587 require clients to use STARTTLS
* to upgrade the connections and also require a username and password to authenticate.
*
*
* The line transport.sendMessage(message, message.allRecipients)
*  generates the following errors after some time running when sending email
*
* It might be due to the fact that it is a companion object and eventually the connection gets closed
*
* For now it is enough to reset Gesticus
*
* But a more compelling solution might be to get a new transport object por every email? Don't think so though...
*
* 451 4.4.2 Timeout - closing connection. 11sm1314198wmd.23 - gsmtp
com.sun.mail.smtp.SMTPSendFailedException: 451 4.4.2 Timeout - closing connection. 11sm1314198wmd.23 - gsmtp
*
* And
*
* javax.mail.MessagingException: Can't send command to SMTP host;
  nested exception is:
	java.net.SocketException: Software caused connection abort: socket write error

*
* */

class GesticusMailUserAgent {

    companion object {

        val logger = java.util.logging.Logger.getGlobal()

        private lateinit var session: Session

        private lateinit var transport: Transport

        private var futures = mutableSetOf<ScheduledFuture<*>>() // MutableSet<ScheduledFuture<*>>()

        init {

            System.setProperty("java.net.preferIPv4Stack", "true")

            val props = Properties().apply {

                put("mail.debug", "true")
                put("mail.transport.protocol", "smtp")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", PORT_SSL)
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
//                put("mail.smtp.starttls.enable", "false")

                put("mail.smtp.socketFactory.port", PORT_SSL) //SSL Port
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            }

            val authenticator = object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(USER_NAME, USER_PASSWORD.decrypt(SECRET_PASSWORD))
//                return PasswordAuthentication(USER_NAME, USER_PASSWORD)
                }
            }

            session = Session.getInstance(props, authenticator)

            transport = session.transport

            transport.connect()

        }


        fun cancelFutures(): Unit {
            futures.forEach {
                if (!it.isCancelled)
                    it.cancel(true)
            }
        }

        /*
        *
        * Sends a message with attachment to multiple recipients
        *
        * */
        private fun send(
                subject: String,
                bodyText: String,
                filenames: List<String>,
                addresses: List<String>
        ): Unit {

            val message: MimeMessage = MimeMessage(session)
            message.setFrom(InternetAddress(USER_NAME))
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(USER_NAME)
            )
            for (address in addresses) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(address))
            }
            message.subject = subject;
            val body = MimeBodyPart()
            // setText(....) is like setContent(..., "text/plain")
            // body.setText(bodyText)
            body.setContent(bodyText, "text/html; charset=utf-8")
            val multiPart = MimeMultipart()
            multiPart.addBodyPart(body)
            filenames.forEach {
                val attachment = MimeBodyPart()
                // Use DataSource with javax 1.3
                // val dataSource = FileDataSource(filenames)
                // Only javax 1.4+
                attachment.attachFile(it)
                multiPart.addBodyPart(attachment)
            }
            message.setContent(multiPart)
            try {
                //Transport.send(message)
                if (transport.isConnected) {
                    transport.sendMessage(message, message.allRecipients)
                }
                else {
                    transport = session.transport
                    transport.sendMessage(message, message.allRecipients)
                }
                val destinataris = addresses.joinToString(", ")
                writeToLog("Message sent the ${message.sentDate} to ${destinataris}")
            } catch (error: Exception) {
                writeToLog("Error sending message: ${error.message}")
                runLater {
                    tornadofx.error(APP_TITLE, error.message)
                }
            }

        }

        /*
         *
         * Sends a message with attachment to multiple recipients.
         *
         * This method schedules unlimited emails
         *
         * */
        fun sendBulkEmailWithAttatchment(
                subject: String,
                bodyText: String,
                filenames: List<String>,
                addresses: List<String>,
                step: Int = 50
        ): Unit {

            futures = mutableSetOf()

            val limit = Math.min(step, GMAIL_LIMIT_PER_HOUR)

            if (addresses.size <= limit) {
                send(subject, bodyText, filenames, addresses)
                return
            }

            // Si hi ha moltes adreces faig subllistes
            val sublists: List<List<String>> = addresses.chunked(limit)

            // nombre de subllistes
            val chuncks = sublists.size

            val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

            /* until excludes upper limit */
            for (i in 0 until chuncks) {

                futures.add(scheduler.schedule(thread(start = false) {
                    send(subject, bodyText, filenames, sublists[i])
                    logger.log(Level.INFO, "Sent chunk $i at ${LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)}")
                }, i.toLong(), TimeUnit.HOURS))

            }

        }

    }

}