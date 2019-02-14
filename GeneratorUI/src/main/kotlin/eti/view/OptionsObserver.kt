package eti.view

import eti.data.Options

interface OptionsObserver {
    fun onOptionschanged(opt: Options)
}