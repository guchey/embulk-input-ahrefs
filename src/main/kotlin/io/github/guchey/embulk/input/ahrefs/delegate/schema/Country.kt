package io.github.guchey.embulk.input.ahrefs.delegate.schema

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

enum class Country {
    AD, AE, AF, AG, AI, AL, AM, AO, AR, AS, AT, AU, AW, AZ, BA, BB, BD, BE, BF, BG, BH, BI, BJ, BN, BO, BR, BS, BT, BW, BY, BZ, CA, CD, CF, CG, CH, CI, CK, CL, CM, CN, CO, CR, CU, CV, CY, CZ, DE, DJ, DK, DM, DO, DZ, EC, EE, EG, ES, ET, FI, FJ, FM, FO, FR, GA, GB, GD, GE, GF, GG, GH, GI, GL, GM, GN, GP, GQ, GR, GT, GU, GY, HK, HN, HR, HT, HU, ID, IE, IL, IM, IN, IQ, IS, IT, JE, JM, JO, JP, KE, KG, KH, KI, KN, KR, KW, KY, KZ, LA, LB, LC, LI, LK, LS, LT, LU, LV, LY, MA, MC, MD, ME, MG, MK, ML, MM, MN, MQ, MR, MS, MT, MU, MV, MW, MX, MY, MZ, NA, NC, NE, NG, NI, NL, NO, NP, NR, NU, NZ, OM, PA, PE, PF, PG, PH, PK, PL, PN, PR, PS, PT, PY, QA, RE, RO, RS, RU, RW, SA, SB, SC, SE, SG, SH, SI, SK, SL, SM, SN, SO, SR, ST, SV, TD, TG, TH, TJ, TK, TL, TM, TN, TO, TR, TT, TW, TZ, UA, UG, US, UY, UZ, VC, VE, VG, VI, VN, VU, WS, YE, YT, ZA, ZM, ZW;

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun of(value: String): Country {
            return Country.valueOf(value.uppercase(Locale.getDefault()))
        }
    }
}