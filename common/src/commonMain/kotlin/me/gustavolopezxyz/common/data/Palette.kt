/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import androidx.compose.ui.graphics.Color

object Palette {
    val Colors = mapOf(
        Pair("gray50", Color(0xFFF9FAFB)),
        Pair("gray100", Color(0xFFF3F4F6)),
        Pair("gray200", Color(0xFFE5E7EB)),
        Pair("gray300", Color(0xFFD1D5DB)),
        Pair("gray400", Color(0xFF9CA3AF)),
        Pair("gray500", Color(0xFF6B7280)),
        Pair("gray600", Color(0xFF4B5563)),
        Pair("gray700", Color(0xFF374151)),
        Pair("gray800", Color(0xFF1F2937)),
        Pair("gray900", Color(0xFF111827)),
        Pair("zinc50", Color(0xFFFAFAFA)),
        Pair("zinc100", Color(0xFFF4F4F5)),
        Pair("zinc200", Color(0xFFE4E4E7)),
        Pair("zinc300", Color(0xFFD4D4D8)),
        Pair("zinc400", Color(0xFFA1A1AA)),
        Pair("zinc500", Color(0xFF71717A)),
        Pair("zinc600", Color(0xFF52525B)),
        Pair("zinc700", Color(0xFF3F3F46)),
        Pair("zinc800", Color(0xFF27272A)),
        Pair("zinc900", Color(0xFF18181B)),
        Pair("red50", Color(0xFFFEF2F2)),
        Pair("red100", Color(0xFFFEE2E2)),
        Pair("red200", Color(0xFFFECACA)),
        Pair("red300", Color(0xFFFCA5A5)),
        Pair("red400", Color(0xFFF87171)),
        Pair("red500", Color(0xFFEF4444)),
        Pair("red600", Color(0xFFDC2626)),
        Pair("red700", Color(0xFFB91C1C)),
        Pair("red800", Color(0xFF991B1B)),
        Pair("red900", Color(0xFF7F1D1D)),
        Pair("orange50", Color(0xFFFFF7ED)),
        Pair("orange100", Color(0xFFFFEDD5)),
        Pair("orange200", Color(0xFFFED7AA)),
        Pair("orange300", Color(0xFFFDBA74)),
        Pair("orange400", Color(0xFFFB923C)),
        Pair("orange500", Color(0xFFF97316)),
        Pair("orange600", Color(0xFFEA580C)),
        Pair("orange700", Color(0xFFC2410C)),
        Pair("orange800", Color(0xFF9A3412)),
        Pair("orange900", Color(0xFF7C2D12)),
        Pair("amber50", Color(0xFFFFFBEB)),
        Pair("amber100", Color(0xFFFEF3C7)),
        Pair("amber200", Color(0xFFFDE68A)),
        Pair("amber300", Color(0xFFFCD34D)),
        Pair("amber400", Color(0xFFFBBF24)),
        Pair("amber500", Color(0xFFF59E0B)),
        Pair("amber600", Color(0xFFD97706)),
        Pair("amber700", Color(0xFFB45309)),
        Pair("amber800", Color(0xFF92400E)),
        Pair("amber900", Color(0xFF78350F)),
        Pair("yellow50", Color(0xFFFEFCE8)),
        Pair("yellow100", Color(0xFFFEF9C3)),
        Pair("yellow200", Color(0xFFFEF08A)),
        Pair("yellow300", Color(0xFFFDE047)),
        Pair("yellow400", Color(0xFFFACC15)),
        Pair("yellow500", Color(0xFFEAB308)),
        Pair("yellow600", Color(0xFFCA8A04)),
        Pair("yellow700", Color(0xFFA16207)),
        Pair("yellow800", Color(0xFF854D0E)),
        Pair("yellow900", Color(0xFF713F12)),
        Pair("lime50", Color(0xFFF7FEE7)),
        Pair("lime100", Color(0xFFECFCCB)),
        Pair("lime200", Color(0xFFD9F99D)),
        Pair("lime300", Color(0xFFBEF264)),
        Pair("lime400", Color(0xFFA3E635)),
        Pair("lime500", Color(0xFF84CC16)),
        Pair("lime600", Color(0xFF65A30D)),
        Pair("lime700", Color(0xFF4D7C0F)),
        Pair("lime800", Color(0xFF3F6212)),
        Pair("lime900", Color(0xFF365314)),
        Pair("green50", Color(0xFFF0FDF4)),
        Pair("green100", Color(0xFFDCFCE7)),
        Pair("green200", Color(0xFFBBF7D0)),
        Pair("green300", Color(0xFF86EFAC)),
        Pair("green400", Color(0xFF4ADE80)),
        Pair("green500", Color(0xFF22C55E)),
        Pair("green600", Color(0xFF16A34A)),
        Pair("green700", Color(0xFF15803D)),
        Pair("green800", Color(0xFF166534)),
        Pair("green900", Color(0xFF14532D)),
        Pair("emerald50", Color(0xFFECFDF5)),
        Pair("emerald100", Color(0xFFD1FAE5)),
        Pair("emerald200", Color(0xFFA7F3D0)),
        Pair("emerald300", Color(0xFF6EE7B7)),
        Pair("emerald400", Color(0xFF34D399)),
        Pair("emerald500", Color(0xFF10B981)),
        Pair("emerald600", Color(0xFF059669)),
        Pair("emerald700", Color(0xFF047857)),
        Pair("emerald800", Color(0xFF065F46)),
        Pair("emerald900", Color(0xFF064E3B)),
        Pair("teal50", Color(0xFFF0FDFA)),
        Pair("teal100", Color(0xFFCCFBF1)),
        Pair("teal200", Color(0xFF99F6E4)),
        Pair("teal300", Color(0xFF5EEAD4)),
        Pair("teal400", Color(0xFF2DD4BF)),
        Pair("teal500", Color(0xFF14B8A6)),
        Pair("teal600", Color(0xFF0D9488)),
        Pair("teal700", Color(0xFF0F766E)),
        Pair("teal800", Color(0xFF115E59)),
        Pair("teal900", Color(0xFF134E4A)),
        Pair("cyan50", Color(0xFFECFEFF)),
        Pair("cyan100", Color(0xFFCFFAFE)),
        Pair("cyan200", Color(0xFFA5F3FC)),
        Pair("cyan300", Color(0xFF67E8F9)),
        Pair("cyan400", Color(0xFF22D3EE)),
        Pair("cyan500", Color(0xFF06B6D4)),
        Pair("cyan600", Color(0xFF0891B2)),
        Pair("cyan700", Color(0xFF0E7490)),
        Pair("cyan800", Color(0xFF155E75)),
        Pair("cyan900", Color(0xFF164E63)),
        Pair("blue50", Color(0xFFEFF6FF)),
        Pair("blue100", Color(0xFFDBEAFE)),
        Pair("blue200", Color(0xFFBFDBFE)),
        Pair("blue300", Color(0xFF93C5FD)),
        Pair("blue400", Color(0xFF60A5FA)),
        Pair("blue500", Color(0xFF3B82F6)),
        Pair("blue600", Color(0xFF2563EB)),
        Pair("blue700", Color(0xFF1D4ED8)),
        Pair("blue800", Color(0xFF1E40AF)),
        Pair("blue900", Color(0xFF1E3A8A)),
        Pair("indigo50", Color(0xFFEEF2FF)),
        Pair("indigo100", Color(0xFFE0E7FF)),
        Pair("indigo200", Color(0xFFC7D2FE)),
        Pair("indigo300", Color(0xFFA5B4FC)),
        Pair("indigo400", Color(0xFF818CF8)),
        Pair("indigo500", Color(0xFF6366F1)),
        Pair("indigo600", Color(0xFF4F46E5)),
        Pair("indigo700", Color(0xFF4338CA)),
        Pair("indigo800", Color(0xFF3730A3)),
        Pair("indigo900", Color(0xFF312E81)),
        Pair("violet50", Color(0xFFF5F3FF)),
        Pair("violet100", Color(0xFFEDE9FE)),
        Pair("violet200", Color(0xFFDDD6FE)),
        Pair("violet300", Color(0xFFC4B5FD)),
        Pair("violet400", Color(0xFFA78BFA)),
        Pair("violet500", Color(0xFF8B5CF6)),
        Pair("violet600", Color(0xFF7C3AED)),
        Pair("violet700", Color(0xFF6D28D9)),
        Pair("violet800", Color(0xFF5B21B6)),
        Pair("violet900", Color(0xFF4C1D95)),
        Pair("purple50", Color(0xFFFAF5FF)),
        Pair("purple100", Color(0xFFF3E8FF)),
        Pair("purple200", Color(0xFFE9D5FF)),
        Pair("purple300", Color(0xFFD8B4FE)),
        Pair("purple400", Color(0xFFC084FC)),
        Pair("purple500", Color(0xFFA855F7)),
        Pair("purple600", Color(0xFF9333EA)),
        Pair("purple700", Color(0xFF7E22CE)),
        Pair("purple800", Color(0xFF6B21A8)),
        Pair("purple900", Color(0xFF581C87)),
        Pair("fuchsia50", Color(0xFFFDF4FF)),
        Pair("fuchsia100", Color(0xFFFAE8FF)),
        Pair("fuchsia200", Color(0xFFF5D0FE)),
        Pair("fuchsia300", Color(0xFFF0ABFC)),
        Pair("fuchsia400", Color(0xFFE879F9)),
        Pair("fuchsia500", Color(0xFFD946EF)),
        Pair("fuchsia600", Color(0xFFC026D3)),
        Pair("fuchsia700", Color(0xFFA21CAF)),
        Pair("fuchsia800", Color(0xFF86198F)),
        Pair("fuchsia900", Color(0xFF701A75)),
        Pair("pink50", Color(0xFFFDF2F8)),
        Pair("pink100", Color(0xFFFCE7F3)),
        Pair("pink200", Color(0xFFFBCFE8)),
        Pair("pink300", Color(0xFFF9A8D4)),
        Pair("pink400", Color(0xFFF472B6)),
        Pair("pink500", Color(0xFFEC4899)),
        Pair("pink600", Color(0xFFDB2777)),
        Pair("pink700", Color(0xFFBE185D)),
        Pair("pink800", Color(0xFF9D174D)),
        Pair("pink900", Color(0xFF831843)),
        Pair("rose50", Color(0xFFFFF1F2)),
        Pair("rose100", Color(0xFFFFE4E6)),
        Pair("rose200", Color(0xFFFECDD3)),
        Pair("rose300", Color(0xFFFDA4AF)),
        Pair("rose400", Color(0xFFFB7185)),
        Pair("rose500", Color(0xFFF43F5E)),
        Pair("rose600", Color(0xFFE11D48)),
        Pair("rose700", Color(0xFFBE123C)),
        Pair("rose800", Color(0xFF9F1239)),
        Pair("rose900", Color(0xFF881337))
    )

    val All: Collection<Color>
        get() = Colors.values

    val Gray: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("gray") }.values

    val Zinc: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("zinc") }.values

    val Red: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("red") }.values

    val Orange: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("orange") }.values

    val Amber: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("amber") }.values

    val Yellow: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("yellow") }.values

    val Lime: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("lime") }.values

    val Green: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("green") }.values

    val Emerald: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("emerald") }.values

    val Teal: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("teal") }.values

    val Cyan: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("cyan") }.values

    val Blue: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("blue") }.values

    val Indigo: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("indigo") }.values

    val Violet: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("violet") }.values

    val Purple: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("purple") }.values

    val Fuchsia: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("fuchsia") }.values

    val Pink: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("pink") }.values

    val Rose: Collection<Color>
        get() = Colors.filterKeys { it.startsWith("rose") }.values

}
