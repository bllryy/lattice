package xyz.meowing.lattice.ui

enum class Pos {
    ParentPercent,
    ScreenPercent,

    ParentPixels,
    ScreenPixels,

    ParentCenter,
    ScreenCenter,

    AfterSibling,
    MatchSibling,
}

enum class Size {
    Auto,
    Percent,
    Pixels,
    Fill,
}

enum class Alignment {
    None,
    Start,
    End,
}

enum class Offset {
    Pixels,
    Percent,
}

enum class TooltipPosition {
    Top,
    Bottom,
    Left,
    Right,
}
