package cat.gencat.access.functions

import org.junit.*


import cat.gencat.access.functions.Utils.Companion.encode
import cat.gencat.access.functions.Utils.Companion.decode
import cat.gencat.access.functions.Utils.Companion.decrypt
import cat.gencat.access.functions.Utils.Companion.encrypt
import cat.gencat.access.functions.Utils.Companion.isValidDniNie
import cat.gencat.access.functions.Utils.Companion.isEmailValid
import cat.gencat.access.functions.Utils.Companion.parseDate
import cat.gencat.access.functions.Utils.Companion.nomPropi
import cat.gencat.access.functions.Utils.Companion.toCatalanDateFormat
import java.time.LocalDate
import java.util.*

class UtilsKtTest {

    @Before
    fun before() {

    }

    @After
    fun after() {

    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeClass() {

        }

        @AfterClass
        @JvmStatic
        fun afterClass() {

        }
    }

    @Test
    fun currentCourseYear() {
        Assert.assertEquals("2018", Utils.currentCourseYear())
    }

    @Test
    fun nextCourseYear() {
        Assert.assertEquals("2019", Utils.nextCourseYear())
    }

    @Test
    fun encode() {
        Assert.assertEquals("c2VjcmV0", "secret".encode())
    }

    @Test
    fun decode() {
        Assert.assertEquals("secret", "c2VjcmV0".decode())
    }

    @Test
    fun encrypt() {
        Assert.assertEquals("sGkYrjwl1RRD8W1Rrf93wA==", "secret".encrypt("secret"))
    }

    @Test
    fun decrypt() {
        Assert.assertEquals("secret", "sGkYrjwl1RRD8W1Rrf93wA==".decrypt("secret"))
    }

    @Test
    fun isValidDniNie() {
        Assert.assertTrue("39164789K".isValidDniNie())
        Assert.assertFalse("39164789J".isValidDniNie())
    }

    @Test
    fun parseDate() {
        Assert.assertEquals(LocalDate.now(), parseDate("01/03/2019"))
    }

    @Test
    fun isEmailValid() {
        Assert.assertTrue(isEmailValid("jmendez1@xtec.cat"))
        Assert.assertFalse(isEmailValid("jmendez1@xtec.cat "))
    }


    @Test
    fun nomPropi() {
        Assert.assertEquals("Pep Mendez Valverde", "PEP MENDEZ VALVERDE".nomPropi())
    }

    @Test
    fun toCatalanFormat() {
        Assert.assertEquals("01/03/2019", Date().toCatalanDateFormat())
    }
}