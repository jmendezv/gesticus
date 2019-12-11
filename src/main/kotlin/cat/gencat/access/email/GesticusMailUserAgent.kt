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
//const val USER_PASSWORD = "frHnSibyReD6nsqPCuG/i0ezR0y9Uh8N"
const val USER_PASSWORD = "pAsCUxmBLCfy6F9iYdUlYuzx2rz2O8S4"

const val SECRET_PASSWORD = "secret"

/*
*
* This port continues to be used primarily for SMTP relaying.
*
* SMTP relaying is the transmittal of email from email server to email server.
*
* In most cases, modern SMTP email clients (Microsoft Outlook, Mail, Thunderbird, etc.)
* shouldn’t use this port. It is traditionally blocked by residential ISPs and
* Cloud Hosting Providers, to curb the amount of spam that is relayed from compromised
* computers or servers.
*
* Unless you’re specifically managing a mail server, you should have no traffic
* traversing this port on your computer or server.
*
* Port 25 is the oldest of the four. It was the port number assigned to SMTP when the protocol
* was first introduced in the now obsolete RFC 821 back in 1982.
*
* However, because this port was often exploited by malicious individuals in order to spread
* spam and malware, it's now blocked by several ISPs.
*
* It is widely used but not for submitting email messages from an email client to an email server.
*
* Rather, it's supposed to be used for relaying messages from one mail server to another mail server.
*
* */
const val PORT_SMTP_25 = "25"
/*
*
* Interestingly, port 465 was never published as an official SMTP transmission or
* submission channel by the IETF.
*
* Instead, the Internet Assigned Numbers Authority (IANA),
* who maintains much of the core internet infrastructure, registered port 465 for SMTPS.
*
* The purpose was to establish a port for SMTP to operate using Secure Sockets Layer (SSL).
*
* SSL is commonly used for encrypting communications over the internet.
*
* This port was first introduced when users started looking for ways to secure email messages.
*
* The idea that emerged then was to encrypt messages using SSL (Secure Sockets Layer).
*
* But at that time, doing so meant using a separate port.
*
* In SMTP, the port chosen for encrypted connections was 465.
*
* Unfortunately, port 465 was never recognized by the IETF (Internet Engineering Task Force)
*
* Today, SMTP can be secured even when using the same port (e.g. 587).
*
* A plaintext SMTP connection can be upgraded to a secure connection encrypted by either TLS (
* Transport Layer Security) or SSL by simply executing the STARTTLS command, provided of course
* the server supports it.
*
* port 465 should no longer be used at all!!!
*
* But I use it anyway and works just fine.
*
* */
const val PORT_SSL = "465"
/*
*
* The RFC 2476 proposed a split of the traditional message submission and message relay concept.
*
* The RFC defined that message submission should occur over port 587 to ensure new policy
* and security requirements don’t interfere with the traditional relay traffic over message
* relay port 25.
*
* This is the default mail submission port. When an email client or server is submitting an email
* to be routed by a proper mail server, it should always use SMTP port 587.
*
* All Mailgun customers should consider using port 587 as default SMTP port unless you’re explicitly
* blocked by your upstream network or hosting provider.
*
* Whereas port 25 is the recommended port number for SMTP communications between mail servers
* port 587 is the one recommended for message submissions by mail clients to mail servers.
*
* Because port 587 is associated with submission servers, then the use of port 587 typically
* implies the use of authentication.
*
* */
const val PORT_TLS = "587"
/*
*
* This port is not endorsed by the IETF nor IANA.
*
* Instead, Mailgun provides it as an alternate port, which mirrors port 587,
* in the event the above ports are blocked.
*
* Because 2525 is a non-traditional high port number, it is typically allowed on consumer
* ISPs and Cloud Hosting providers, like Google Compute Engine.
*
* If you’ve tried the above ports, but experience connectivity issues, try port 2525.
*
* This port also supports TLS encryption.
*
* */
const val PORT_SMTP_2525 = "2525"

const val GMAIL_LIMIT_PER_HOUR = 99

/*
*
* First ever email 1971
*
* SSL and TLS both provide a way to encrypt a communication channel between two computers.
*
* TLS is the successor of SSL and both terms are used interchangeably in general.
*
* STARTTLS is a way to tackle an existing insecure connections and upgrade it to a secure
* connection using SSL/TLS.
*
* Version numbering is inconsistent between SSL and TLS. When TLS took over from SSL as the
* preferred protocol name, it began a new version number, and also began using sub-versions.
*
* SSL v2, SSL v3, TLS v1.0, TLS v1.1, TLS v1.2, TLS v.1.3
*
* SMTP (Simple Mail Transfer Protocol) uses port 23 and was originally designed for transfer,
* not submission.
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

        private val props = Properties().apply {

            put("mail.debug", "true")
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", PORT_SSL)
            put("mail.smtp.auth", "true")
            /*
            *
            * Start Transport Layer Security
            *
            * E-mail servers and clients that uses the SMTP protocol normally communicate using
            * plain text over the Internet.
            *
            * To improve security, an encrypted TLS (Transport Layer Security) connection can be used
            * when communicating between the e-mail server and the client.
            *
            * TLS is most useful when a login username and password (sent by the AUTH command) needs
            * to be encrypted.
            *
            * TLS can be used to encrypt the whole e-mail message, but the command does not guarantee
            * that the whole message will stay encrypted the whole way to the receiver;
            *
            * some e-mail servers can decide to send the e-mail message with no encryption.
            *
            * But at least the username and password used with the AUTH command will stay encrypted.
            *
            * Using the STARTTLS command together with the AUTH command is a very secure way to authenticate
            * users.
            *
            * */
            put("mail.smtp.starttls.enable", "true")
//                put("mail.smtp.starttls.enable", "false")

            put("mail.smtp.socketFactory.port", PORT_SSL) //SSL Port
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        }

        private val authenticator = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(USER_NAME, USER_PASSWORD.decrypt(SECRET_PASSWORD))
//                return PasswordAuthentication(USER_NAME, USER_PASSWORD)
            }
        }

        init {

            System.setProperty("java.net.preferIPv4Stack", "true")

            session = Session.getInstance(props, authenticator)

            transport = session.transport

            transport.connect()

        }


        fun cancelFutures(): Unit {
            futures.forEach {
                if (!it.isCancelled)
                    it.cancel(true)
            }
            futures.clear()
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
                if (!transport.isConnected) {
                    session = Session.getInstance(props, authenticator)
                    transport = session.transport
                    transport.connect()
                }
                transport.sendMessage(message, message.allRecipients)
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