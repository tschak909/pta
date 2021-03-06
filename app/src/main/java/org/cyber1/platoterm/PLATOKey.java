/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;


import org.jetbrains.annotations.Contract;

/**
 * PLATOKey, translation classes to turn Android Keycodes into PLATO keys.
 * Created by thomc on 3/3/2018.
 */

class PLATOKey {

    static final int SOFTKEY_LT = -30;
    static final int SOFTKEY_GT = -31;
    static final int SOFTKEY_LEFT_BRACKET = -32;
    static final int SOFTKEY_RIGHT_BRACKET = -33;
    static final int SOFTKEY_DOLLAR = -34;
    static final int SOFTKEY_PERCENT = -35;
    static final int SOFTKEY_UNDERLINE = -36;
    static final int SOFTKEY_APOSTROPHE = -37;
    static final int SOFTKEY_ASTERISK = -38;
    static final int SOFTKEY_LEFT_PAREN = -39;
    static final int SOFTKEY_RIGHT_PAREN = -40;
    static final int SOFTKEY_AT = -41;
    static final int SOFTKEY_POUND = -42;
    static final int SOFTKEY_CARET = -43;
    static final int SOFTKEY_AMPERSAND = -44;
    static final int SOFTKEY_LEFT_BRACE = -45;
    static final int SOFTKEY_RIGHT_BRACE = -46;
    static final int SOFTKEY_BACKSLASH = -47;
    static final int SOFTKEY_VERTICAL_BAR = -48;
    static final int SOFTKEY_GRAVE = -49;
    static final int SOFTKEY_TILDE = -50;
    static final int SOFTKEY_TAB = -51;
    static final int SOFTKEY_DIVIDE = -52;
    static final int SOFTKEY_MINUS = -53;
    static final int SOFTKEY_PLUS = -54;
    static final int SOFTKEY_ANS = -10;
    static final int SOFTKEY_BACK = -11;
    static final int SOFTKEY_COPY = -12;
    static final int SOFTKEY_DATA = -13;
    static final int SOFTKEY_EDIT = -14;
    static final int SOFTKEY_FONT = -15;
    static final int SOFTKEY_HELP = -16;
    static final int SOFTKEY_LAB = -17;
    static final int SOFTKEY_MICRO = -18;
    static final int SOFTKEY_SQUARE = -20;
    static final int SOFTKEY_TERM = -22;
    static final int SOFTKEY_SUPER = -23;
    static final int SOFTKEY_SUB = -24;
    static final int SOFTKEY_STOP = -25;
    static final int SOFTKEY_MULTIPLY = -55;
    static final int SOFTKEY_ASSIGN = -56;
    static final int SOFTKEY_SIGMA = -57;

    /**
     * Android key code to ASCII translation table, unshifted.
     */
    private static final int[] keycodeToPLATO = {
            0xFF,   // AKEYCODE_UNKNOWN
            0xFF,   // AKEYCODE_SOFT_LEFT
            0xFF,   // AKEYCODE_SOFT_RIGHT
            0xFF,   // AKEYCODE_HOME
            0xFF,   // AKEYCODE_BACK
            0xFF,   // AKEYCODE_CALL
            0xFF,   // AKEYCODE_ENDCALL
            0x00,   // AKEYCODE_0
            0x01,   // AKEYCODE_1
            0x02,   // AKEYCODE_2
            0x03,   // AKEYCODE_3
            0x04,   // AKEYCODE_4
            0x05,   // AKEYCODE_5
            0x06,   // AKEYCODE_6
            0x07,   // AKEYCODE_7
            0x08,   // AKEYCODE_8
            0x09,   // AKEYCODE_9
            0x28,   // AKEYCODE_STAR
            0xFF,   // AKEYCODE_POUND TODO: (Access key)
            0xFF,   // AKEYCODE_DPAD_UP
            0xFF,   // AKEYCODE_DPAD_DOWN
            0xFF,   // AKEYCODE_DPAD_LEFT
            0xFF,   // AKEYCODE_DPAD_RIGHT
            0xFF,   // AKEYCODE_DPAD_CENTER
            0xFF,   // AKEYCODE_VOLUME_UP
            0xFF,   // AKEYCODE_VOLUME_DOWN
            0xFF,   // AKEYCODE_POWER
            0xFF,   // AKEYCODE_CAMERA
            0xFF,   // AKEYCODE_CLEAR
            0x41,   // AKEYCODE_A
            0x42,   // AKEYCODE_B
            0x43,   // AKEYCODE_C
            0x44,   // AKEYCODE_D
            0x45,   // AKEYCODE_E
            0x46,   // AKEYCODE_F
            0x47,   // AKEYCODE_G
            0x48,   // AKEYCODE_H
            0x49,   // AKEYCODE_I
            0x4A,   // AKEYCODE_J
            0x4B,   // AKEYCODE_K
            0x4C,   // AKEYCODE_L
            0x4D,   // AKEYCODE_M
            0x4E,   // AKEYCODE_N
            0x4F,   // AKEYCODE_O
            0x50,   // AKEYCODE_P
            0x51,   // AKEYCODE_Q
            0x52,   // AKEYCODE_R
            0x53,   // AKEYCODE_S
            0x54,   // AKEYCODE_T
            0x55,   // AKEYCODE_U
            0x56,   // AKEYCODE_V
            0x57,   // AKEYCODE_W
            0x58,   // AKEYCODE_X
            0x59,   // AKEYCODE_Y
            0x5A,   // AKEYCODE_Z
            0x5F,   // AKEYCODE_COMMA
            0x5E,   // AKEYCODE_PERIOD
            0xFF,   // AKEYCODE_ALT_LEFT
            0xFF,   // AKEYCODE_ALT_RIGHT
            0xFF,   // AKEYCODE_SHIFT_LEFT
            0xFF,   // AKEYCODE_SHIFT_RIGHT
            0x0C,   // AKEYCODE_TAB
            0x40,   // AKEYCODE_SPACE
            0xFF,   // AKEYCODE_SYM
            0xFF,   // AKEYCODE_EXPLORER
            0xFF,   // AKEYCODE_ENVELOPE
            0x16,   // AKEYCODE_ENTER
            0x13,   // AKEYCODE_DEL
            0x7E,   // AKEYCODE_GRAVE
            0x0F,   // AKEYCODE_MINUS
            0x5B,   // AKEYCODE_EQUALS
            0x22,   // AKEYCODE_LEFT_BRACKET
            0x23,   // AKEYCODE_RIGHT_BRACKET
            0xFF,   // AKEYCODE_BACKSLASH TODO: (access key)
            0x5C,   // AKEYCODE_SEMICOLON
            0x27,   // AKEYCODE_APOSTROPHE
            0x5D,   // AKEYCODE_SLASH
            0xFF,   // AKEYCODE_AT TODO (access key)
            0xFF,   // AKEYCODE_NUM
            0xFF,   // AKEYCODE_HEADSETHOOK
            0xFF,   // AKEYCODE_FOCUS
            0xFF,   // AKEYCODE_PLUS
            0xFF,   // AKEYCODE_MENU
            0xFF,   // AKEYCODE_NOTIFICATION
            0xFF,   // AKEYCODE_SEARCH
            0xFF,   // AKEYCODE_MEDIA_PLAY_PAUSE
            0xFF,   // AKEYCODE_STOP
            0xFF,   // AKEYCODE_MEDIA_NEXT
            0xFF,   // AKEYCODE_MEDIA_PREVIOUS
            0xFF,   // AKEYCODE_MEDIA_REWIND
            0xFF,   // AKEYCODE_FAST_FORWARD
            0xFF,   // AKEYCODE_MUTE
            0xFF,   // AKEYCODE_PAGE_UP
            0xFF,   // AKEYCODE_PAGE_DOWN
            0xFF,   // AKEYCODE_PICTSYMBOLS
            0xFF,   // AKEYCODE_SWITCH_CHARSET
            0xFF,   // AKEYCODE_BUTTON_A
            0xFF,   // AKEYCODE_BUTTON_B
            0xFF,   // AKEYCODE_BUTTON_C
            0xFF,   // AKEYCODE_BUTTON_X
            0xFF,   // AKEYCODE_BUTTON_Y
            0xFF,   // AKEYCODE_BUTTON_Z
            0xFF,   // AKEYCODE_BUTTON_L1
            0xFF,   // AKEYCODE_BUTTON_R1
            0xFF,   // AKEYCODE_BUTTON_L2
            0xFF,   // AKEYCODE_BUTTON_R2
            0xFF,   // AKEYCODE_BUTTON_THUMBL
            0xFF,   // AKEYCODE_BUTTON_THUMBR
            0xFF,   // AKEYCODE_BUTTON_START
            0xFF,   // AKEYCODE_BUTTON_SELECT
            0xFF,   // AKEYCODE_BUTTON_MODE
            0x0D,   // AKEYCODE_ESCAPE
            0x13,   // AKEYCODE_FORWARD_DEL
            0xFF,   // AKEYCODE_CTRL_LEFT
            0xFF,   // AKEYCODE_CTRL_RIGHT
            0xFF,   // AKEYCODE_CAPS_LOCK
            0xFF,   // AKEYCODE_SCROLL_LOCK
            0xFF,   // AKEYCODE_META_LEFT
            0xFF,   // AKEYCODE_META_RIGHT
            0xFF,   // AKEYCODE_FUNCTION
            0xFF,   // AKEYCODE_SYSRQ
            0xFF,   // AKEYCODE_BREAK
            0xFF,   // AKEYCODE_MOVE_HOME
            0xFF,   // AKEYCODE_MOVE_END
            0xFF,   // AKEYCODE_INSERT
            0xFF,   // AKEYCODE_FORWARD
            0xFF,   // AKEYCODE_MEDIA_PLAY
            0xFF,   // AKEYCODE_MEDIA_PAUSE
            0xFF,   // AKEYCODE_MEDIA_CLOSE
            0xFF,   // AKEYCODE_MEDIA_EJECT
            0xFF,   // AKEYCODE_MEDIA_RECORD
            0x1B,   // AKEYCODE_F1
            0x12,   // AKEYCODE_F2
            0xFF,   // AKEYCODE_F3  (TODO: Another access key???) (Square)
            0x14,   // AKEYCODE_F4
            0x17,   // AKEYCODE_F5
            0x15,   // AKEYCODE_F6
            0x1D,   // AKEYCODE_F7
            0x18,   // AKEYCODE_F8
            0x19,   // AKEYCODE_F9
            0x1A,   // AKEYCODE_F10
            0xFF,   // AKEYCODE_F11
            0xFF,   // AKEYCODE_F12
            0xFF,   // AKEYCODE_NUM_LOCK
            0x00,   // AKEYCODE_NUMPAD_0
            0x01,   // AKEYCODE_NUMPAD_1
            0x02,   // AKEYCODE_NUMPAD_2
            0x03,   // AKEYCODE_NUMPAD_3
            0x04,   // AKEYCODE_NUMPAD_4
            0x05,   // AKEYCODE_NUMPAD_5
            0x06,   // AKEYCODE_NUMPAD_6
            0x07,   // AKEYCODE_NUMPAD_7
            0x08,   // AKEYCODE_NUMPAD_8
            0x09,   // AKEYCODE_NUMPAD_9
            0x0B,   // AKEYCODE_NUMPAD_DIVIDE
            0x0A,   // AKEYCODE_NUMPAD_MULTIPLY
            0x0f,   // AKEYCODE_NUMPAD_SUBTRACT
            0x0E,   // AKEYCODE_NUMPAD_ADD
            0x5E,   // AKEYCODE_NUMPAD_DOT
            0x5F,   // AKEYCODE_NUMPAD_COMMA
            0x26,   // AKEYCODE_NUMPAD_ENTER
            0x5B,   // AKEYCODE_NUMPAD_EQUALS
            0x09,   // AKEYCODE_NUMPAD_LEFT_PAREN
            0x5B,   // AKEYCODE_NUMPAD_RIGHT_PAREN
            0xFF,   // AKEYCODE_VOLUME_MUTE
            0xFF,   // AKEYCODE_INFO
            0xFF,   // AKEYCODE_CHANNEL_UP
            0xFF,   // AKEYCODE_CHANNEL_DOWN
            0xFF,   // AKEYCODE_ZOOM_IN
            0xFF,   // AKEYCODE_ZOOM_OUT
            0xFF,   // AKEYCODE_TV
            0xFF,   // AKEYCODE_WINDOW
            0xFF,   // AKEYCODE_GUIDE
            0xFF,   // AKEYCODE_DVR
            0xFF,   // AKEYCODE_BOOKMARK
            0xFF,   // AKEYCODE_CAPTIONS
            0xFF,   // AKEYCODE_SETTINGS
            0xFF,   // AKEYCODE_TV_POWER
            0xFF,   // AKEYCODE_TV_INPUT
            0xFF,   // AKEYCODE_STB_POWER
            0xFF,   // AKEYCODE_STB_INPUT
            0xFF,   // AKEYCODE_AVR_POWER
            0xFF,   // AKEYCODE_AVR_INPUT
            0xFF,   // AKEYCODE_PROG_RED
            0xFF,   // AKEYCODE_PROG_GREEN
            0xFF,   // AKEYCODE_PROG_YELLOW
            0xFF,   // AKEYCODE_PROG_BLUE
            0xFF,   // AKEYCODE_APP_SWITCH
            0xFF,   // AKEYCODE_BUTTON_1
            0xFF,   // AKEYCODE_BUTTON_2
            0xFF,   // AKEYCODE_BUTTON_3
            0xFF,   // AKEYCODE_BUTTON_4
            0xFF,   // AKEYCODE_BUTTON_5
            0xFF,   // AKEYCODE_BUTTON_6
            0xFF,   // AKEYCODE_BUTTON_7
            0xFF,   // AKEYCODE_BUTTON_8
            0xFF,   // AKEYCODE_BUTTON_9
            0xFF,   // AKEYCODE_BUTTON_10
            0xFF,   // AKEYCODE_BUTTON_11
            0xFF,   // AKEYCODE_BUTTON_12
            0xFF,   // AKEYCODE_BUTTON_13
            0xFF,   // AKEYCODE_BUTTON_14
            0xFF,   // AKEYCODE_BUTTON_15
            0xFF,   // AKEYCODE_BUTTON_16
            0xFF,   // AKEYCODE_LANGUAGE_SWITCH
            0xFF,   // AKEYCODE_MANNER_MODE
            0xFF,   // AKEYCODE_3D_MODE
            0xFF,   // AKEYCODE_CONTACTS
            0xFF,   // AKEYCODE_CALENDAR
            0xFF,   // AKEYCODE_ZENKAKU_HANAKANKU
            0xFF,   // AKEYCODE_EISU
            0xFF,   // AKEYCODE_MUHENKAN
            0xFF,   // AKEYCODE_HENKAN
            0xFF,   // AKEYCODE_KATAKANA_HIRAGANA
            0xFF,   // AKEYCODE_YEN
            0xFF,   // AKEYCODE_RO
            0xFF,   // AKEYCODE_KANA
            0xFF,   // AKEYCODE_ASSIST
            0xFF,   // AKEYCODE_BRIGHTNESS_DOWN
            0xFF,   // AKEYCODE_BRIGHTNESS_UP
            0xFF,   // AKEYCODE_MEDIA_AUDIO_TRACK
            0xFF,   // AKEYCODE_SLEEP
            0xFF,   // AKEYCODE_WAKEUP
            0xFF,   // AKEYCODE_PAIRING
            0xFF,   // AKEYCODE_MEDIA_TOP_MENU
            0xFF,   // AKEYCODE_11
            0xFF,   // AKEYCODE_12
            0xFF,   // AKEYCODE_LAST_CHANNEL
            0xFF,   // AKEYCODE_TV_DATA_SERVICE
            0xFF,   // AKEYCODE_VOICE_ASSIST
            0xFF,   // AKEYCODE_TV_RADIO_SERVICE
            0xFF,   // AKEYCODE_TELETEXT
            0xFF,   // AKEYCODE_TV_NUMBER_ENTRY
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_ANALOG
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_DIGITAL
            0xFF,   // AKEYCODE_TV_SATELLITE
            0xFF,   // AKEYCODE_TV_SATELLITE_BS
            0xFF,   // AKEYCODE_TV_SATELLITE_CS
            0xFF,   // AKEYCODE_TV_SATELLITE_SERVICE
            0xFF,   // AKEYCODE_TV_NETWORK
            0xFF,   // AKEYCODE_TV_ANTENNA_CABLE
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_1
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_2
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_3
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_4
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_2
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_2
            0xFF,   // AKEYCODE_TV_INPUT_VGA_1
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN
            0xFF,   // AKEYCODE_TV_ZOOM_MODE
            0xFF,   // AKEYCODE_TV_CONTENTS_MENU
            0xFF,   // AKEYCODE_TV_MEDIA_CONTEXT_MENU
            0xFF,   // AKEYCODE_TV_TIMER_PROGRAMMING
            0x15,   // AKEYCODE_HELP
            0xFF,   // AKEYCODE_NAVIGATE_PREVIOUS
            0xFF,   // AKEYCODE_NAVIGATE_NEXT
            0xFF,   // AKEYCODE_NAVIGATE_IN
            0xFF,   // AKEYCODE_NAVIGATE_OUT
            0xFF,   // AKEYCODE_STEM_PRIMARY
            0xFF,   // AKEYCODE_STEM_1
            0xFF,   // AKEYCODE_STEM_2
            0xFF,   // AKEYCODE_STEM_3
            0xFF,   // AKEYCODE_DPAD_UP_LEFT
            0xFF,   // AKEYCODE_DPAD_DOWN_LEFT
            0xFF,   // AKEYCODE_DPAD_UP_RIGHT
            0xFF,   // AKEYCODE_DPAD_DOWN_RIGHT
            0xFF,   // AKEYCODE_MEDIA_SKIP_FORWARD
            0xFF,   // AKEYCODE_SOFT_SLEEP
            0xFF,   // AKEYCODE_CUT
            0xFF,   // AKEYCODE_COPY
            0xFF,   // AKEYCODE_PASTE
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_UP
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_DOWN
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_LEFT
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_RIGHT
            0xFF    // AKEYCODE_ALL_APPS
    };

    /**
     * Android key code to ASCII translation table, unshifted.
     */
    private static final int[] keycodeToShiftedPLATO = {
            0xFF,   // AKEYCODE_UNKNOWN
            0xFF,   // AKEYCODE_SOFT_LEFT
            0xFF,   // AKEYCODE_SOFT_RIGHT
            0xFF,   // AKEYCODE_HOME
            0xFF,   // AKEYCODE_BACK
            0xFF,   // AKEYCODE_CALL
            0xFF,   // AKEYCODE_ENDCALL
            0x20,   // AKEYCODE_0
            0x21,   // AKEYCODE_1
            0x22,   // AKEYCODE_2
            0x23,   // AKEYCODE_3
            0x24,   // AKEYCODE_4
            0x25,   // AKEYCODE_5
            0x26,   // AKEYCODE_6
            0x27,   // AKEYCODE_7
            0x28,   // AKEYCODE_8
            0x29,   // AKEYCODE_9
            0x28,   // AKEYCODE_STAR
            0xFF,   // AKEYCODE_POUND TODO: (Access key)
            0xFF,   // AKEYCODE_DPAD_UP
            0xFF,   // AKEYCODE_DPAD_DOWN
            0xFF,   // AKEYCODE_DPAD_LEFT
            0xFF,   // AKEYCODE_DPAD_RIGHT
            0xFF,   // AKEYCODE_DPAD_CENTER
            0xFF,   // AKEYCODE_VOLUME_UP
            0xFF,   // AKEYCODE_VOLUME_DOWN
            0xFF,   // AKEYCODE_POWER
            0xFF,   // AKEYCODE_CAMERA
            0xFF,   // AKEYCODE_CLEAR
            0x61,   // AKEYCODE_A
            0x62,   // AKEYCODE_B
            0x63,   // AKEYCODE_C
            0x64,   // AKEYCODE_D
            0x65,   // AKEYCODE_E
            0x66,   // AKEYCODE_F
            0x67,   // AKEYCODE_G
            0x68,   // AKEYCODE_H
            0x69,   // AKEYCODE_I
            0x6A,   // AKEYCODE_J
            0x6B,   // AKEYCODE_K
            0x6C,   // AKEYCODE_L
            0x6D,   // AKEYCODE_M
            0x6E,   // AKEYCODE_N
            0x6F,   // AKEYCODE_O
            0x70,   // AKEYCODE_P
            0x71,   // AKEYCODE_Q
            0x72,   // AKEYCODE_R
            0x73,   // AKEYCODE_S
            0x74,   // AKEYCODE_T
            0x75,   // AKEYCODE_U
            0x76,   // AKEYCODE_V
            0x77,   // AKEYCODE_W
            0x78,   // AKEYCODE_X
            0x79,   // AKEYCODE_Y
            0x7A,   // AKEYCODE_Z
            0x20,   // AKEYCODE_COMMA
            0x21,   // AKEYCODE_PERIOD
            0xFF,   // AKEYCODE_ALT_LEFT
            0xFF,   // AKEYCODE_ALT_RIGHT
            0xFF,   // AKEYCODE_SHIFT_LEFT
            0xFF,   // AKEYCODE_SHIFT_RIGHT
            0x2C,   // AKEYCODE_TAB
            0x40,   // AKEYCODE_SPACE
            0xFF,   // AKEYCODE_SYM
            0xFF,   // AKEYCODE_EXPLORER
            0xFF,   // AKEYCODE_ENVELOPE
            0x16,   // AKEYCODE_ENTER
            0x33,   // AKEYCODE_DEL
            0x7E,   // AKEYCODE_GRAVE
            0x0F,   // AKEYCODE_MINUS
            0x7B,   // AKEYCODE_EQUALS
            0x22,   // AKEYCODE_LEFT_BRACKET
            0x23,   // AKEYCODE_RIGHT_BRACKET
            0xFF,   // AKEYCODE_BACKSLASH TODO: (access key)
            0x5C,   // AKEYCODE_SEMICOLON
            0x27,   // AKEYCODE_APOSTROPHE
            0x5D,   // AKEYCODE_SLASH
            0xFF,   // AKEYCODE_AT TODO (access key)
            0xFF,   // AKEYCODE_NUM
            0xFF,   // AKEYCODE_HEADSETHOOK
            0xFF,   // AKEYCODE_FOCUS
            0xFF,   // AKEYCODE_PLUS
            0xFF,   // AKEYCODE_MENU
            0xFF,   // AKEYCODE_NOTIFICATION
            0xFF,   // AKEYCODE_SEARCH
            0xFF,   // AKEYCODE_MEDIA_PLAY_PAUSE
            0xFF,   // AKEYCODE_STOP
            0xFF,   // AKEYCODE_MEDIA_NEXT
            0xFF,   // AKEYCODE_MEDIA_PREVIOUS
            0xFF,   // AKEYCODE_MEDIA_REWIND
            0xFF,   // AKEYCODE_FAST_FORWARD
            0xFF,   // AKEYCODE_MUTE
            0xFF,   // AKEYCODE_PAGE_UP
            0xFF,   // AKEYCODE_PAGE_DOWN
            0xFF,   // AKEYCODE_PICTSYMBOLS
            0xFF,   // AKEYCODE_SWITCH_CHARSET
            0xFF,   // AKEYCODE_BUTTON_A
            0xFF,   // AKEYCODE_BUTTON_B
            0xFF,   // AKEYCODE_BUTTON_C
            0xFF,   // AKEYCODE_BUTTON_X
            0xFF,   // AKEYCODE_BUTTON_Y
            0xFF,   // AKEYCODE_BUTTON_Z
            0xFF,   // AKEYCODE_BUTTON_L1
            0xFF,   // AKEYCODE_BUTTON_R1
            0xFF,   // AKEYCODE_BUTTON_L2
            0xFF,   // AKEYCODE_BUTTON_R2
            0xFF,   // AKEYCODE_BUTTON_THUMBL
            0xFF,   // AKEYCODE_BUTTON_THUMBR
            0xFF,   // AKEYCODE_BUTTON_START
            0xFF,   // AKEYCODE_BUTTON_SELECT
            0xFF,   // AKEYCODE_BUTTON_MODE
            0xFF,   // AKEYCODE_ESCAPE
            0x60,   // AKEYCODE_FORWARD_DEL
            0xFF,   // AKEYCODE_CTRL_LEFT
            0xFF,   // AKEYCODE_CTRL_RIGHT
            0xFF,   // AKEYCODE_CAPS_LOCK
            0xFF,   // AKEYCODE_SCROLL_LOCK
            0xFF,   // AKEYCODE_META_LEFT
            0xFF,   // AKEYCODE_META_RIGHT
            0xFF,   // AKEYCODE_FUNCTION
            0xFF,   // AKEYCODE_SYSRQ
            0xFF,   // AKEYCODE_BREAK
            0xFF,   // AKEYCODE_MOVE_HOME
            0xFF,   // AKEYCODE_MOVE_END
            0xFF,   // AKEYCODE_INSERT
            0xFF,   // AKEYCODE_FORWARD
            0xFF,   // AKEYCODE_MEDIA_PLAY
            0xFF,   // AKEYCODE_MEDIA_PAUSE
            0xFF,   // AKEYCODE_MEDIA_CLOSE
            0xFF,   // AKEYCODE_MEDIA_EJECT
            0xFF,   // AKEYCODE_MEDIA_RECORD
            0x3B,   // AKEYCODE_F1
            0x32,   // AKEYCODE_F2
            0xFF,   // AKEYCODE_F3  (TODO: Another access key???) (Square)
            0x34,   // AKEYCODE_F4
            0x37,   // AKEYCODE_F5
            0x35,   // AKEYCODE_F6
            0x3D,   // AKEYCODE_F7
            0x38,   // AKEYCODE_F8
            0x39,   // AKEYCODE_F9
            0x3A,   // AKEYCODE_F10
            0xFF,   // AKEYCODE_F11
            0xFF,   // AKEYCODE_F12
            0xFF,   // AKEYCODE_NUM_LOCK
            0x00,   // AKEYCODE_NUMPAD_0
            0x01,   // AKEYCODE_NUMPAD_1
            0x02,   // AKEYCODE_NUMPAD_2
            0x03,   // AKEYCODE_NUMPAD_3
            0x04,   // AKEYCODE_NUMPAD_4
            0x05,   // AKEYCODE_NUMPAD_5
            0x06,   // AKEYCODE_NUMPAD_6
            0x07,   // AKEYCODE_NUMPAD_7
            0x08,   // AKEYCODE_NUMPAD_8
            0x09,   // AKEYCODE_NUMPAD_9
            0x0B,   // AKEYCODE_NUMPAD_DIVIDE
            0x0A,   // AKEYCODE_NUMPAD_MULTIPLY
            0x0f,   // AKEYCODE_NUMPAD_SUBTRACT
            0x0E,   // AKEYCODE_NUMPAD_ADD
            0x5E,   // AKEYCODE_NUMPAD_DOT
            0x5F,   // AKEYCODE_NUMPAD_COMMA
            0x26,   // AKEYCODE_NUMPAD_ENTER
            0x5B,   // AKEYCODE_NUMPAD_EQUALS
            0x09,   // AKEYCODE_NUMPAD_LEFT_PAREN
            0x5B,   // AKEYCODE_NUMPAD_RIGHT_PAREN
            0xFF,   // AKEYCODE_VOLUME_MUTE
            0xFF,   // AKEYCODE_INFO
            0xFF,   // AKEYCODE_CHANNEL_UP
            0xFF,   // AKEYCODE_CHANNEL_DOWN
            0xFF,   // AKEYCODE_ZOOM_IN
            0xFF,   // AKEYCODE_ZOOM_OUT
            0xFF,   // AKEYCODE_TV
            0xFF,   // AKEYCODE_WINDOW
            0xFF,   // AKEYCODE_GUIDE
            0xFF,   // AKEYCODE_DVR
            0xFF,   // AKEYCODE_BOOKMARK
            0xFF,   // AKEYCODE_CAPTIONS
            0xFF,   // AKEYCODE_SETTINGS
            0xFF,   // AKEYCODE_TV_POWER
            0xFF,   // AKEYCODE_TV_INPUT
            0xFF,   // AKEYCODE_STB_POWER
            0xFF,   // AKEYCODE_STB_INPUT
            0xFF,   // AKEYCODE_AVR_POWER
            0xFF,   // AKEYCODE_AVR_INPUT
            0xFF,   // AKEYCODE_PROG_RED
            0xFF,   // AKEYCODE_PROG_GREEN
            0xFF,   // AKEYCODE_PROG_YELLOW
            0xFF,   // AKEYCODE_PROG_BLUE
            0xFF,   // AKEYCODE_APP_SWITCH
            0xFF,   // AKEYCODE_BUTTON_1
            0xFF,   // AKEYCODE_BUTTON_2
            0xFF,   // AKEYCODE_BUTTON_3
            0xFF,   // AKEYCODE_BUTTON_4
            0xFF,   // AKEYCODE_BUTTON_5
            0xFF,   // AKEYCODE_BUTTON_6
            0xFF,   // AKEYCODE_BUTTON_7
            0xFF,   // AKEYCODE_BUTTON_8
            0xFF,   // AKEYCODE_BUTTON_9
            0xFF,   // AKEYCODE_BUTTON_10
            0xFF,   // AKEYCODE_BUTTON_11
            0xFF,   // AKEYCODE_BUTTON_12
            0xFF,   // AKEYCODE_BUTTON_13
            0xFF,   // AKEYCODE_BUTTON_14
            0xFF,   // AKEYCODE_BUTTON_15
            0xFF,   // AKEYCODE_BUTTON_16
            0xFF,   // AKEYCODE_LANGUAGE_SWITCH
            0xFF,   // AKEYCODE_MANNER_MODE
            0xFF,   // AKEYCODE_3D_MODE
            0xFF,   // AKEYCODE_CONTACTS
            0xFF,   // AKEYCODE_CALENDAR
            0xFF,   // AKEYCODE_ZENKAKU_HANAKANKU
            0xFF,   // AKEYCODE_EISU
            0xFF,   // AKEYCODE_MUHENKAN
            0xFF,   // AKEYCODE_HENKAN
            0xFF,   // AKEYCODE_KATAKANA_HIRAGANA
            0xFF,   // AKEYCODE_YEN
            0xFF,   // AKEYCODE_RO
            0xFF,   // AKEYCODE_KANA
            0xFF,   // AKEYCODE_ASSIST
            0xFF,   // AKEYCODE_BRIGHTNESS_DOWN
            0xFF,   // AKEYCODE_BRIGHTNESS_UP
            0xFF,   // AKEYCODE_MEDIA_AUDIO_TRACK
            0xFF,   // AKEYCODE_SLEEP
            0xFF,   // AKEYCODE_WAKEUP
            0xFF,   // AKEYCODE_PAIRING
            0xFF,   // AKEYCODE_MEDIA_TOP_MENU
            0xFF,   // AKEYCODE_11
            0xFF,   // AKEYCODE_12
            0xFF,   // AKEYCODE_LAST_CHANNEL
            0xFF,   // AKEYCODE_TV_DATA_SERVICE
            0xFF,   // AKEYCODE_VOICE_ASSIST
            0xFF,   // AKEYCODE_TV_RADIO_SERVICE
            0xFF,   // AKEYCODE_TELETEXT
            0xFF,   // AKEYCODE_TV_NUMBER_ENTRY
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_ANALOG
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_DIGITAL
            0xFF,   // AKEYCODE_TV_SATELLITE
            0xFF,   // AKEYCODE_TV_SATELLITE_BS
            0xFF,   // AKEYCODE_TV_SATELLITE_CS
            0xFF,   // AKEYCODE_TV_SATELLITE_SERVICE
            0xFF,   // AKEYCODE_TV_NETWORK
            0xFF,   // AKEYCODE_TV_ANTENNA_CABLE
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_1
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_2
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_3
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_4
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_2
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_2
            0xFF,   // AKEYCODE_TV_INPUT_VGA_1
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN
            0xFF,   // AKEYCODE_TV_ZOOM_MODE
            0xFF,   // AKEYCODE_TV_CONTENTS_MENU
            0xFF,   // AKEYCODE_TV_MEDIA_CONTEXT_MENU
            0xFF,   // AKEYCODE_TV_TIMER_PROGRAMMING
            0x15,   // AKEYCODE_HELP
            0xFF,   // AKEYCODE_NAVIGATE_PREVIOUS
            0xFF,   // AKEYCODE_NAVIGATE_NEXT
            0xFF,   // AKEYCODE_NAVIGATE_IN
            0xFF,   // AKEYCODE_NAVIGATE_OUT
            0xFF,   // AKEYCODE_STEM_PRIMARY
            0xFF,   // AKEYCODE_STEM_1
            0xFF,   // AKEYCODE_STEM_2
            0xFF,   // AKEYCODE_STEM_3
            0xFF,   // AKEYCODE_DPAD_UP_LEFT
            0xFF,   // AKEYCODE_DPAD_DOWN_LEFT
            0xFF,   // AKEYCODE_DPAD_UP_RIGHT
            0xFF,   // AKEYCODE_DPAD_DOWN_RIGHT
            0xFF,   // AKEYCODE_MEDIA_SKIP_FORWARD
            0xFF,   // AKEYCODE_SOFT_SLEEP
            0xFF,   // AKEYCODE_CUT
            0xFF,   // AKEYCODE_COPY
            0xFF,   // AKEYCODE_PASTE
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_UP
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_DOWN
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_LEFT
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_RIGHT
            0xFF    // AKEYCODE_ALL_APPS
    };

    /**
     * Android key code to ASCII translation table, CTRL pressed.
     */
    private static final int[] ctrlKeycodeToPLATO = {
            0xFF,   // AKEYCODE_UNKNOWN
            0xFF,   // AKEYCODE_SOFT_LEFT
            0xFF,   // AKEYCODE_SOFT_RIGHT
            0xFF,   // AKEYCODE_HOME
            0xFF,   // AKEYCODE_BACK
            0xFF,   // AKEYCODE_CALL
            0xFF,   // AKEYCODE_ENDCALL
            0xFF,   // AKEYCODE_0
            0xFF,   // AKEYCODE_1
            0xFF,   // AKEYCODE_2
            0xFF,   // AKEYCODE_3
            0xFF,   // AKEYCODE_4
            0xFF,   // AKEYCODE_5
            0xFF,   // AKEYCODE_6
            0xFF,   // AKEYCODE_7
            0xFF,   // AKEYCODE_8
            0xFF,   // AKEYCODE_9
            0xFF,   // AKEYCODE_STAR
            0xFF,   // AKEYCODE_POUND TODO: (Access key)
            0xFF,   // AKEYCODE_DPAD_UP
            0xFF,   // AKEYCODE_DPAD_DOWN
            0xFF,   // AKEYCODE_DPAD_LEFT
            0xFF,   // AKEYCODE_DPAD_RIGHT
            0xFF,   // AKEYCODE_DPAD_CENTER
            0xFF,   // AKEYCODE_VOLUME_UP
            0xFF,   // AKEYCODE_VOLUME_DOWN
            0xFF,   // AKEYCODE_POWER
            0xFF,   // AKEYCODE_CAMERA
            0xFF,   // AKEYCODE_CLEAR
            0x12,   // AKEYCODE_A
            0x18,   // AKEYCODE_B
            0x1C,   // AKEYCODE_C
            0x19,   // AKEYCODE_D
            0x17,   // AKEYCODE_E
            0x34,   // AKEYCODE_F
            0x0B,   // AKEYCODE_G
            0x15,   // AKEYCODE_H
            0xFF,   // AKEYCODE_I
            0xFF,   // AKEYCODE_J
            0xFF,   // AKEYCODE_K
            0x1D,   // AKEYCODE_L
            0x14,   // AKEYCODE_M
            0x16,   // AKEYCODE_N
            0xFF,   // AKEYCODE_O
            0x10,   // AKEYCODE_P
            0x1C,   // AKEYCODE_Q
            0x13,   // AKEYCODE_R
            0x1A,   // AKEYCODE_S
            0x32,   // AKEYCODE_T
            0xFF,   // AKEYCODE_U
            0xFF,   // AKEYCODE_V
            0xFF,   // AKEYCODE_W
            0x0A,   // AKEYCODE_X
            0x11,   // AKEYCODE_Y
            0xFF,   // AKEYCODE_Z
            0xFF,   // AKEYCODE_COMMA
            0xFF,   // AKEYCODE_PERIOD
            0xFF,   // AKEYCODE_ALT_LEFT
            0xFF,   // AKEYCODE_ALT_RIGHT
            0xFF,   // AKEYCODE_SHIFT_LEFT
            0xFF,   // AKEYCODE_SHIFT_RIGHT
            0xFF,   // AKEYCODE_TAB
            0xFF,   // AKEYCODE_SPACE
            0xFF,   // AKEYCODE_SYM
            0xFF,   // AKEYCODE_EXPLORER
            0xFF,   // AKEYCODE_ENVELOPE
            0x36,   // AKEYCODE_ENTER
            0xFF,   // AKEYCODE_DEL
            0xFF,   // AKEYCODE_GRAVE
            0x2F,   // AKEYCODE_MINUS
            0xFF,   // AKEYCODE_EQUALS
            0xFF,   // AKEYCODE_LEFT_BRACKET
            0xFF,   // AKEYCODE_RIGHT_BRACKET
            0xFF,   // AKEYCODE_BACKSLASH TODO: (access key)
            0xFF,   // AKEYCODE_SEMICOLON
            0xFF,   // AKEYCODE_APOSTROPHE
            0xFF,   // AKEYCODE_SLASH
            0xFF,   // AKEYCODE_AT TODO (access key)
            0xFF,   // AKEYCODE_NUM
            0xFF,   // AKEYCODE_HEADSETHOOK
            0xFF,   // AKEYCODE_FOCUS
            0xFF,   // AKEYCODE_PLUS
            0xFF,   // AKEYCODE_MENU
            0xFF,   // AKEYCODE_NOTIFICATION
            0xFF,   // AKEYCODE_SEARCH
            0xFF,   // AKEYCODE_MEDIA_PLAY_PAUSE
            0xFF,   // AKEYCODE_STOP
            0xFF,   // AKEYCODE_MEDIA_NEXT
            0xFF,   // AKEYCODE_MEDIA_PREVIOUS
            0xFF,   // AKEYCODE_MEDIA_REWIND
            0xFF,   // AKEYCODE_FAST_FORWARD
            0xFF,   // AKEYCODE_MUTE
            0xFF,   // AKEYCODE_PAGE_UP
            0xFF,   // AKEYCODE_PAGE_DOWN
            0xFF,   // AKEYCODE_PICTSYMBOLS
            0xFF,   // AKEYCODE_SWITCH_CHARSET
            0xFF,   // AKEYCODE_BUTTON_A
            0xFF,   // AKEYCODE_BUTTON_B
            0xFF,   // AKEYCODE_BUTTON_C
            0xFF,   // AKEYCODE_BUTTON_X
            0xFF,   // AKEYCODE_BUTTON_Y
            0xFF,   // AKEYCODE_BUTTON_Z
            0xFF,   // AKEYCODE_BUTTON_L1
            0xFF,   // AKEYCODE_BUTTON_R1
            0xFF,   // AKEYCODE_BUTTON_L2
            0xFF,   // AKEYCODE_BUTTON_R2
            0xFF,   // AKEYCODE_BUTTON_THUMBL
            0xFF,   // AKEYCODE_BUTTON_THUMBR
            0xFF,   // AKEYCODE_BUTTON_START
            0xFF,   // AKEYCODE_BUTTON_SELECT
            0xFF,   // AKEYCODE_BUTTON_MODE
            0xFF,   // AKEYCODE_ESCAPE
            0xFF,   // AKEYCODE_FORWARD_DEL
            0xFF,   // AKEYCODE_CTRL_LEFT
            0xFF,   // AKEYCODE_CTRL_RIGHT
            0xFF,   // AKEYCODE_CAPS_LOCK
            0xFF,   // AKEYCODE_SCROLL_LOCK
            0xFF,   // AKEYCODE_META_LEFT
            0xFF,   // AKEYCODE_META_RIGHT
            0xFF,   // AKEYCODE_FUNCTION
            0xFF,   // AKEYCODE_SYSRQ
            0xFF,   // AKEYCODE_BREAK
            0xFF,   // AKEYCODE_MOVE_HOME
            0xFF,   // AKEYCODE_MOVE_END
            0xFF,   // AKEYCODE_INSERT
            0xFF,   // AKEYCODE_FORWARD
            0xFF,   // AKEYCODE_MEDIA_PLAY
            0xFF,   // AKEYCODE_MEDIA_PAUSE
            0xFF,   // AKEYCODE_MEDIA_CLOSE
            0xFF,   // AKEYCODE_MEDIA_EJECT
            0xFF,   // AKEYCODE_MEDIA_RECORD
            0xFF,   // AKEYCODE_F1
            0xFF,   // AKEYCODE_F2
            0xFF,   // AKEYCODE_F3  (TODO: Another access key???) (Square)
            0xFF,   // AKEYCODE_F4
            0xFF,   // AKEYCODE_F5
            0xFF,   // AKEYCODE_F6
            0xFF,   // AKEYCODE_F7
            0xFF,   // AKEYCODE_F8
            0xFF,   // AKEYCODE_F9
            0xFF,   // AKEYCODE_F10
            0xFF,   // AKEYCODE_F11
            0xFF,   // AKEYCODE_F12
            0xFF,   // AKEYCODE_NUM_LOCK
            0xFF,   // AKEYCODE_NUMPAD_0
            0xFF,   // AKEYCODE_NUMPAD_1
            0xFF,   // AKEYCODE_NUMPAD_2
            0xFF,   // AKEYCODE_NUMPAD_3
            0xFF,   // AKEYCODE_NUMPAD_4
            0xFF,   // AKEYCODE_NUMPAD_5
            0xFF,   // AKEYCODE_NUMPAD_6
            0xFF,   // AKEYCODE_NUMPAD_7
            0xFF,   // AKEYCODE_NUMPAD_8
            0xFF,   // AKEYCODE_NUMPAD_9
            0xFF,   // AKEYCODE_NUMPAD_DIVIDE
            0xFF,   // AKEYCODE_NUMPAD_MULTIPLY
            0xFF,   // AKEYCODE_NUMPAD_SUBTRACT
            0xFF,   // AKEYCODE_NUMPAD_ADD
            0xFF,   // AKEYCODE_NUMPAD_DOT
            0xFF,   // AKEYCODE_NUMPAD_COMMA
            0xFF,   // AKEYCODE_NUMPAD_ENTER
            0xFF,   // AKEYCODE_NUMPAD_EQUALS
            0xFF,   // AKEYCODE_NUMPAD_LEFT_PAREN
            0xFF,   // AKEYCODE_NUMPAD_RIGHT_PAREN
            0xFF,   // AKEYCODE_VOLUME_MUTE
            0xFF,   // AKEYCODE_INFO
            0xFF,   // AKEYCODE_CHANNEL_UP
            0xFF,   // AKEYCODE_CHANNEL_DOWN
            0xFF,   // AKEYCODE_ZOOM_IN
            0xFF,   // AKEYCODE_ZOOM_OUT
            0xFF,   // AKEYCODE_TV
            0xFF,   // AKEYCODE_WINDOW
            0xFF,   // AKEYCODE_GUIDE
            0xFF,   // AKEYCODE_DVR
            0xFF,   // AKEYCODE_BOOKMARK
            0xFF,   // AKEYCODE_CAPTIONS
            0xFF,   // AKEYCODE_SETTINGS
            0xFF,   // AKEYCODE_TV_POWER
            0xFF,   // AKEYCODE_TV_INPUT
            0xFF,   // AKEYCODE_STB_POWER
            0xFF,   // AKEYCODE_STB_INPUT
            0xFF,   // AKEYCODE_AVR_POWER
            0xFF,   // AKEYCODE_AVR_INPUT
            0xFF,   // AKEYCODE_PROG_RED
            0xFF,   // AKEYCODE_PROG_GREEN
            0xFF,   // AKEYCODE_PROG_YELLOW
            0xFF,   // AKEYCODE_PROG_BLUE
            0xFF,   // AKEYCODE_APP_SWITCH
            0xFF,   // AKEYCODE_BUTTON_1
            0xFF,   // AKEYCODE_BUTTON_2
            0xFF,   // AKEYCODE_BUTTON_3
            0xFF,   // AKEYCODE_BUTTON_4
            0xFF,   // AKEYCODE_BUTTON_5
            0xFF,   // AKEYCODE_BUTTON_6
            0xFF,   // AKEYCODE_BUTTON_7
            0xFF,   // AKEYCODE_BUTTON_8
            0xFF,   // AKEYCODE_BUTTON_9
            0xFF,   // AKEYCODE_BUTTON_10
            0xFF,   // AKEYCODE_BUTTON_11
            0xFF,   // AKEYCODE_BUTTON_12
            0xFF,   // AKEYCODE_BUTTON_13
            0xFF,   // AKEYCODE_BUTTON_14
            0xFF,   // AKEYCODE_BUTTON_15
            0xFF,   // AKEYCODE_BUTTON_16
            0xFF,   // AKEYCODE_LANGUAGE_SWITCH
            0xFF,   // AKEYCODE_MANNER_MODE
            0xFF,   // AKEYCODE_3D_MODE
            0xFF,   // AKEYCODE_CONTACTS
            0xFF,   // AKEYCODE_CALENDAR
            0xFF,   // AKEYCODE_ZENKAKU_HANAKANKU
            0xFF,   // AKEYCODE_EISU
            0xFF,   // AKEYCODE_MUHENKAN
            0xFF,   // AKEYCODE_HENKAN
            0xFF,   // AKEYCODE_KATAKANA_HIRAGANA
            0xFF,   // AKEYCODE_YEN
            0xFF,   // AKEYCODE_RO
            0xFF,   // AKEYCODE_KANA
            0xFF,   // AKEYCODE_ASSIST
            0xFF,   // AKEYCODE_BRIGHTNESS_DOWN
            0xFF,   // AKEYCODE_BRIGHTNESS_UP
            0xFF,   // AKEYCODE_MEDIA_AUDIO_TRACK
            0xFF,   // AKEYCODE_SLEEP
            0xFF,   // AKEYCODE_WAKEUP
            0xFF,   // AKEYCODE_PAIRING
            0xFF,   // AKEYCODE_MEDIA_TOP_MENU
            0xFF,   // AKEYCODE_11
            0xFF,   // AKEYCODE_12
            0xFF,   // AKEYCODE_LAST_CHANNEL
            0xFF,   // AKEYCODE_TV_DATA_SERVICE
            0xFF,   // AKEYCODE_VOICE_ASSIST
            0xFF,   // AKEYCODE_TV_RADIO_SERVICE
            0xFF,   // AKEYCODE_TELETEXT
            0xFF,   // AKEYCODE_TV_NUMBER_ENTRY
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_ANALOG
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_DIGITAL
            0xFF,   // AKEYCODE_TV_SATELLITE
            0xFF,   // AKEYCODE_TV_SATELLITE_BS
            0xFF,   // AKEYCODE_TV_SATELLITE_CS
            0xFF,   // AKEYCODE_TV_SATELLITE_SERVICE
            0xFF,   // AKEYCODE_TV_NETWORK
            0xFF,   // AKEYCODE_TV_ANTENNA_CABLE
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_1
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_2
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_3
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_4
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_2
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_2
            0xFF,   // AKEYCODE_TV_INPUT_VGA_1
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN
            0xFF,   // AKEYCODE_TV_ZOOM_MODE
            0xFF,   // AKEYCODE_TV_CONTENTS_MENU
            0xFF,   // AKEYCODE_TV_MEDIA_CONTEXT_MENU
            0xFF,   // AKEYCODE_TV_TIMER_PROGRAMMING
            0xFF,   // AKEYCODE_HELP
            0xFF,   // AKEYCODE_NAVIGATE_PREVIOUS
            0xFF,   // AKEYCODE_NAVIGATE_NEXT
            0xFF,   // AKEYCODE_NAVIGATE_IN
            0xFF,   // AKEYCODE_NAVIGATE_OUT
            0xFF,   // AKEYCODE_STEM_PRIMARY
            0xFF,   // AKEYCODE_STEM_1
            0xFF,   // AKEYCODE_STEM_2
            0xFF,   // AKEYCODE_STEM_3
            0xFF,   // AKEYCODE_DPAD_UP_LEFT
            0xFF,   // AKEYCODE_DPAD_DOWN_LEFT
            0xFF,   // AKEYCODE_DPAD_UP_RIGHT
            0xFF,   // AKEYCODE_DPAD_DOWN_RIGHT
            0xFF,   // AKEYCODE_MEDIA_SKIP_FORWARD
            0xFF,   // AKEYCODE_SOFT_SLEEP
            0xFF,   // AKEYCODE_CUT
            0xFF,   // AKEYCODE_COPY
            0xFF,   // AKEYCODE_PASTE
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_UP
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_DOWN
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_LEFT
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_RIGHT
            0xFF    // AKEYCODE_ALL_APPS
    };

    /**
     * Android key code to ASCII translation table, CTRL pressed.
     */
    private static final int[] ctrlKeycodeToShiftedPLATO = {
            0xFF,   // AKEYCODE_UNKNOWN
            0xFF,   // AKEYCODE_SOFT_LEFT
            0xFF,   // AKEYCODE_SOFT_RIGHT
            0xFF,   // AKEYCODE_HOME
            0xFF,   // AKEYCODE_BACK
            0xFF,   // AKEYCODE_CALL
            0xFF,   // AKEYCODE_ENDCALL
            0xFF,   // AKEYCODE_0
            0xFF,   // AKEYCODE_1
            0xFF,   // AKEYCODE_2
            0xFF,   // AKEYCODE_3
            0xFF,   // AKEYCODE_4
            0xFF,   // AKEYCODE_5
            0xFF,   // AKEYCODE_6
            0xFF,   // AKEYCODE_7
            0xFF,   // AKEYCODE_8
            0xFF,   // AKEYCODE_9
            0xFF,   // AKEYCODE_STAR
            0xFF,   // AKEYCODE_POUND TODO: (Access key)
            0xFF,   // AKEYCODE_DPAD_UP
            0xFF,   // AKEYCODE_DPAD_DOWN
            0xFF,   // AKEYCODE_DPAD_LEFT
            0xFF,   // AKEYCODE_DPAD_RIGHT
            0xFF,   // AKEYCODE_DPAD_CENTER
            0xFF,   // AKEYCODE_VOLUME_UP
            0xFF,   // AKEYCODE_VOLUME_DOWN
            0xFF,   // AKEYCODE_POWER
            0xFF,   // AKEYCODE_CAMERA
            0xFF,   // AKEYCODE_CLEAR
            0xFF,   // AKEYCODE_A
            0x38,   // AKEYCODE_B
            0x3B,   // AKEYCODE_C
            0x39,   // AKEYCODE_D
            0x33,   // AKEYCODE_E
            0x34,   // AKEYCODE_F
            0xFF,   // AKEYCODE_G
            0x35,   // AKEYCODE_H
            0xFF,   // AKEYCODE_I
            0xFF,   // AKEYCODE_J
            0xFF,   // AKEYCODE_K
            0x3D,   // AKEYCODE_L
            0x14,   // AKEYCODE_M
            0x36,   // AKEYCODE_N
            0xFF,   // AKEYCODE_O
            0x30,   // AKEYCODE_P
            0x3C,   // AKEYCODE_Q
            0x33,   // AKEYCODE_R
            0x3A,   // AKEYCODE_S
            0x32,   // AKEYCODE_T
            0xFF,   // AKEYCODE_U
            0xFF,   // AKEYCODE_V
            0xFF,   // AKEYCODE_W
            0xFF,   // AKEYCODE_X
            0x31,   // AKEYCODE_Y
            0xFF,   // AKEYCODE_Z
            0xFF,   // AKEYCODE_COMMA
            0xFF,   // AKEYCODE_PERIOD
            0xFF,   // AKEYCODE_ALT_LEFT
            0xFF,   // AKEYCODE_ALT_RIGHT
            0xFF,   // AKEYCODE_SHIFT_LEFT
            0xFF,   // AKEYCODE_SHIFT_RIGHT
            0xFF,   // AKEYCODE_TAB
            0xFF,   // AKEYCODE_SPACE
            0xFF,   // AKEYCODE_SYM
            0xFF,   // AKEYCODE_EXPLORER
            0xFF,   // AKEYCODE_ENVELOPE
            0xFF,   // AKEYCODE_ENTER
            0xFF,   // AKEYCODE_DEL
            0xFF,   // AKEYCODE_GRAVE
            0xFF,   // AKEYCODE_MINUS
            0xFF,   // AKEYCODE_EQUALS
            0xFF,   // AKEYCODE_LEFT_BRACKET
            0xFF,   // AKEYCODE_RIGHT_BRACKET
            0xFF,   // AKEYCODE_BACKSLASH TODO: (access key)
            0xFF,   // AKEYCODE_SEMICOLON
            0xFF,   // AKEYCODE_APOSTROPHE
            0xFF,   // AKEYCODE_SLASH
            0xFF,   // AKEYCODE_AT TODO (access key)
            0xFF,   // AKEYCODE_NUM
            0xFF,   // AKEYCODE_HEADSETHOOK
            0xFF,   // AKEYCODE_FOCUS
            0xFF,   // AKEYCODE_PLUS
            0xFF,   // AKEYCODE_MENU
            0xFF,   // AKEYCODE_NOTIFICATION
            0xFF,   // AKEYCODE_SEARCH
            0xFF,   // AKEYCODE_MEDIA_PLAY_PAUSE
            0xFF,   // AKEYCODE_STOP
            0xFF,   // AKEYCODE_MEDIA_NEXT
            0xFF,   // AKEYCODE_MEDIA_PREVIOUS
            0xFF,   // AKEYCODE_MEDIA_REWIND
            0xFF,   // AKEYCODE_FAST_FORWARD
            0xFF,   // AKEYCODE_MUTE
            0xFF,   // AKEYCODE_PAGE_UP
            0xFF,   // AKEYCODE_PAGE_DOWN
            0xFF,   // AKEYCODE_PICTSYMBOLS
            0xFF,   // AKEYCODE_SWITCH_CHARSET
            0xFF,   // AKEYCODE_BUTTON_A
            0xFF,   // AKEYCODE_BUTTON_B
            0xFF,   // AKEYCODE_BUTTON_C
            0xFF,   // AKEYCODE_BUTTON_X
            0xFF,   // AKEYCODE_BUTTON_Y
            0xFF,   // AKEYCODE_BUTTON_Z
            0xFF,   // AKEYCODE_BUTTON_L1
            0xFF,   // AKEYCODE_BUTTON_R1
            0xFF,   // AKEYCODE_BUTTON_L2
            0xFF,   // AKEYCODE_BUTTON_R2
            0xFF,   // AKEYCODE_BUTTON_THUMBL
            0xFF,   // AKEYCODE_BUTTON_THUMBR
            0xFF,   // AKEYCODE_BUTTON_START
            0xFF,   // AKEYCODE_BUTTON_SELECT
            0xFF,   // AKEYCODE_BUTTON_MODE
            0xFF,   // AKEYCODE_ESCAPE
            0xFF,   // AKEYCODE_FORWARD_DEL
            0xFF,   // AKEYCODE_CTRL_LEFT
            0xFF,   // AKEYCODE_CTRL_RIGHT
            0xFF,   // AKEYCODE_CAPS_LOCK
            0xFF,   // AKEYCODE_SCROLL_LOCK
            0xFF,   // AKEYCODE_META_LEFT
            0xFF,   // AKEYCODE_META_RIGHT
            0xFF,   // AKEYCODE_FUNCTION
            0xFF,   // AKEYCODE_SYSRQ
            0xFF,   // AKEYCODE_BREAK
            0xFF,   // AKEYCODE_MOVE_HOME
            0xFF,   // AKEYCODE_MOVE_END
            0xFF,   // AKEYCODE_INSERT
            0xFF,   // AKEYCODE_FORWARD
            0xFF,   // AKEYCODE_MEDIA_PLAY
            0xFF,   // AKEYCODE_MEDIA_PAUSE
            0xFF,   // AKEYCODE_MEDIA_CLOSE
            0xFF,   // AKEYCODE_MEDIA_EJECT
            0xFF,   // AKEYCODE_MEDIA_RECORD
            0xFF,   // AKEYCODE_F1
            0xFF,   // AKEYCODE_F2
            0xFF,   // AKEYCODE_F3  (TODO: Another access key???) (Square)
            0xFF,   // AKEYCODE_F4
            0xFF,   // AKEYCODE_F5
            0xFF,   // AKEYCODE_F6
            0xFF,   // AKEYCODE_F7
            0xFF,   // AKEYCODE_F8
            0xFF,   // AKEYCODE_F9
            0xFF,   // AKEYCODE_F10
            0xFF,   // AKEYCODE_F11
            0xFF,   // AKEYCODE_F12
            0xFF,   // AKEYCODE_NUM_LOCK
            0xFF,   // AKEYCODE_NUMPAD_0
            0xFF,   // AKEYCODE_NUMPAD_1
            0xFF,   // AKEYCODE_NUMPAD_2
            0xFF,   // AKEYCODE_NUMPAD_3
            0xFF,   // AKEYCODE_NUMPAD_4
            0xFF,   // AKEYCODE_NUMPAD_5
            0xFF,   // AKEYCODE_NUMPAD_6
            0xFF,   // AKEYCODE_NUMPAD_7
            0xFF,   // AKEYCODE_NUMPAD_8
            0xFF,   // AKEYCODE_NUMPAD_9
            0xFF,   // AKEYCODE_NUMPAD_DIVIDE
            0xFF,   // AKEYCODE_NUMPAD_MULTIPLY
            0xFF,   // AKEYCODE_NUMPAD_SUBTRACT
            0xFF,   // AKEYCODE_NUMPAD_ADD
            0xFF,   // AKEYCODE_NUMPAD_DOT
            0xFF,   // AKEYCODE_NUMPAD_COMMA
            0xFF,   // AKEYCODE_NUMPAD_ENTER
            0xFF,   // AKEYCODE_NUMPAD_EQUALS
            0xFF,   // AKEYCODE_NUMPAD_LEFT_PAREN
            0xFF,   // AKEYCODE_NUMPAD_RIGHT_PAREN
            0xFF,   // AKEYCODE_VOLUME_MUTE
            0xFF,   // AKEYCODE_INFO
            0xFF,   // AKEYCODE_CHANNEL_UP
            0xFF,   // AKEYCODE_CHANNEL_DOWN
            0xFF,   // AKEYCODE_ZOOM_IN
            0xFF,   // AKEYCODE_ZOOM_OUT
            0xFF,   // AKEYCODE_TV
            0xFF,   // AKEYCODE_WINDOW
            0xFF,   // AKEYCODE_GUIDE
            0xFF,   // AKEYCODE_DVR
            0xFF,   // AKEYCODE_BOOKMARK
            0xFF,   // AKEYCODE_CAPTIONS
            0xFF,   // AKEYCODE_SETTINGS
            0xFF,   // AKEYCODE_TV_POWER
            0xFF,   // AKEYCODE_TV_INPUT
            0xFF,   // AKEYCODE_STB_POWER
            0xFF,   // AKEYCODE_STB_INPUT
            0xFF,   // AKEYCODE_AVR_POWER
            0xFF,   // AKEYCODE_AVR_INPUT
            0xFF,   // AKEYCODE_PROG_RED
            0xFF,   // AKEYCODE_PROG_GREEN
            0xFF,   // AKEYCODE_PROG_YELLOW
            0xFF,   // AKEYCODE_PROG_BLUE
            0xFF,   // AKEYCODE_APP_SWITCH
            0xFF,   // AKEYCODE_BUTTON_1
            0xFF,   // AKEYCODE_BUTTON_2
            0xFF,   // AKEYCODE_BUTTON_3
            0xFF,   // AKEYCODE_BUTTON_4
            0xFF,   // AKEYCODE_BUTTON_5
            0xFF,   // AKEYCODE_BUTTON_6
            0xFF,   // AKEYCODE_BUTTON_7
            0xFF,   // AKEYCODE_BUTTON_8
            0xFF,   // AKEYCODE_BUTTON_9
            0xFF,   // AKEYCODE_BUTTON_10
            0xFF,   // AKEYCODE_BUTTON_11
            0xFF,   // AKEYCODE_BUTTON_12
            0xFF,   // AKEYCODE_BUTTON_13
            0xFF,   // AKEYCODE_BUTTON_14
            0xFF,   // AKEYCODE_BUTTON_15
            0xFF,   // AKEYCODE_BUTTON_16
            0xFF,   // AKEYCODE_LANGUAGE_SWITCH
            0xFF,   // AKEYCODE_MANNER_MODE
            0xFF,   // AKEYCODE_3D_MODE
            0xFF,   // AKEYCODE_CONTACTS
            0xFF,   // AKEYCODE_CALENDAR
            0xFF,   // AKEYCODE_ZENKAKU_HANAKANKU
            0xFF,   // AKEYCODE_EISU
            0xFF,   // AKEYCODE_MUHENKAN
            0xFF,   // AKEYCODE_HENKAN
            0xFF,   // AKEYCODE_KATAKANA_HIRAGANA
            0xFF,   // AKEYCODE_YEN
            0xFF,   // AKEYCODE_RO
            0xFF,   // AKEYCODE_KANA
            0xFF,   // AKEYCODE_ASSIST
            0xFF,   // AKEYCODE_BRIGHTNESS_DOWN
            0xFF,   // AKEYCODE_BRIGHTNESS_UP
            0xFF,   // AKEYCODE_MEDIA_AUDIO_TRACK
            0xFF,   // AKEYCODE_SLEEP
            0xFF,   // AKEYCODE_WAKEUP
            0xFF,   // AKEYCODE_PAIRING
            0xFF,   // AKEYCODE_MEDIA_TOP_MENU
            0xFF,   // AKEYCODE_11
            0xFF,   // AKEYCODE_12
            0xFF,   // AKEYCODE_LAST_CHANNEL
            0xFF,   // AKEYCODE_TV_DATA_SERVICE
            0xFF,   // AKEYCODE_VOICE_ASSIST
            0xFF,   // AKEYCODE_TV_RADIO_SERVICE
            0xFF,   // AKEYCODE_TELETEXT
            0xFF,   // AKEYCODE_TV_NUMBER_ENTRY
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_ANALOG
            0xFF,   // AKEYCODE_TV_TERRESTRIAL_DIGITAL
            0xFF,   // AKEYCODE_TV_SATELLITE
            0xFF,   // AKEYCODE_TV_SATELLITE_BS
            0xFF,   // AKEYCODE_TV_SATELLITE_CS
            0xFF,   // AKEYCODE_TV_SATELLITE_SERVICE
            0xFF,   // AKEYCODE_TV_NETWORK
            0xFF,   // AKEYCODE_TV_ANTENNA_CABLE
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_1
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_2
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_3
            0xFF,   // AKEYCODE_TV_INPUT_HDMI_4
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPOSITE_2
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_1
            0xFF,   // AKEYCODE_TV_INPUT_COMPONENT_2
            0xFF,   // AKEYCODE_TV_INPUT_VGA_1
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP
            0xFF,   // AKEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN
            0xFF,   // AKEYCODE_TV_ZOOM_MODE
            0xFF,   // AKEYCODE_TV_CONTENTS_MENU
            0xFF,   // AKEYCODE_TV_MEDIA_CONTEXT_MENU
            0xFF,   // AKEYCODE_TV_TIMER_PROGRAMMING
            0xFF,   // AKEYCODE_HELP
            0xFF,   // AKEYCODE_NAVIGATE_PREVIOUS
            0xFF,   // AKEYCODE_NAVIGATE_NEXT
            0xFF,   // AKEYCODE_NAVIGATE_IN
            0xFF,   // AKEYCODE_NAVIGATE_OUT
            0xFF,   // AKEYCODE_STEM_PRIMARY
            0xFF,   // AKEYCODE_STEM_1
            0xFF,   // AKEYCODE_STEM_2
            0xFF,   // AKEYCODE_STEM_3
            0xFF,   // AKEYCODE_DPAD_UP_LEFT
            0xFF,   // AKEYCODE_DPAD_DOWN_LEFT
            0xFF,   // AKEYCODE_DPAD_UP_RIGHT
            0xFF,   // AKEYCODE_DPAD_DOWN_RIGHT
            0xFF,   // AKEYCODE_MEDIA_SKIP_FORWARD
            0xFF,   // AKEYCODE_SOFT_SLEEP
            0xFF,   // AKEYCODE_CUT
            0xFF,   // AKEYCODE_COPY
            0xFF,   // AKEYCODE_PASTE
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_UP
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_DOWN
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_LEFT
            0xFF,   // AKEYCODE_SYSTEM_NAVIGATION_RIGHT
            0xFF    // AKEYCODE_ALL_APPS
    };

    private PLATOKey() {
        // NOP
    }

    /**
     * Given an Android AKEYCODE, return the PLATO code
     *
     * @param keyCode Android AKEYCODE from Android's input system
     * @return The PLATO code.
     */
    @Contract(pure = true)
    static int getPLATOcodeForKeycode(int keyCode) {
        if (keyCode > -1 && keyCode < keycodeToPLATO.length)
            return keycodeToPLATO[keyCode];

        return 0xff;
    }

    /**
     * Given an Android AKEYCODE, return the shifted PLATO code
     *
     * @param keyCode Android AKEYCODE from Android's input system
     * @return The PLATO code.
     */
    @Contract(pure = true)
    static int getShiftedPLATOcodeForKeycode(int keyCode) {
        if (keyCode > -1 && keyCode < keycodeToShiftedPLATO.length)
            return keycodeToShiftedPLATO[keyCode];

        return 0xff;
    }

    /**
     * Given an Android AKEYCODE, that is also holding AKEYCODE_CTRL,
     * Return the PLATO code.
     *
     * @param keyCode Android AKEYCODE from the Android's input system
     * @return The PLATO code
     */
    static int getPLATOcodeForCTRLKeycode(int keyCode) {
        if (keyCode > -1 && keyCode < ctrlKeycodeToPLATO.length)
            return ctrlKeycodeToPLATO[keyCode];

        return 0xff;
    }

    static int getShiftedPLATOcodeForCTRLKeycode(int keyCode) {
        if (keyCode > -1 && keyCode < ctrlKeycodeToShiftedPLATO.length)
            return ctrlKeycodeToShiftedPLATO[keyCode];

        return 0xff;
    }

}
