package com.example.olx.helper

import android.app.Activity
import android.content.SharedPreferences
import com.example.olx.model.State
import com.example.olx.model.Filter

class SPFilters {

    companion object {
        const val PREFERENCE_FILE = "PREFERENCE_FILE"

        fun setFilters(activity: Activity, chave: String, valor: String) {
            val preferences = activity.getSharedPreferences(PREFERENCE_FILE, 0)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString(chave, valor)
            editor.apply()
        }

        fun getFilters(activity: Activity): Filter {
            val preferences = activity.getSharedPreferences(PREFERENCE_FILE, 0)

            val search = preferences.getString("search", "")
            val stateUF = preferences.getString("stateUF", "")
            val category = preferences.getString("category", "")
            val stateName = preferences.getString("stateName", "")
            val region = preferences.getString("region", "")
            val ddd = preferences.getString("ddd", "")

            var valueMin = 0f
            if (preferences.getString("valueMin", "")?.isNotEmpty() == true) {
                valueMin = preferences.getFloat("valueMin", 0f)
            }

            var valueMax = 0f
            if (preferences.getString("valueMax", "")?.isNotEmpty() == true) {
                valueMax = preferences.getFloat("valueMax", 0f)
            }

            val state = State(
                uf = stateUF ?: "",
                region = region ?: "",
                name = stateName ?: "",
                ddd = ddd ?: ""
            )

            return Filter(
                state = state,
                category = category ?: "",
                search = search ?: "",
                valueMin = valueMin,
                valueMax = valueMax
            )
        }

        fun isFilter(activity: Activity): Boolean {
            val filter = getFilters(activity)

            return when {
                filter.state.uf.isNotEmpty() || filter.state.region.isNotEmpty() ||
                        filter.state.name.isNotEmpty() || filter.state.ddd.isNotEmpty() -> {
                    true
                }
                filter.category.isNotEmpty() || filter.search.isNotEmpty() -> {
                    true
                }
                filter.valueMin > 0f || filter.valueMax > 0f -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        fun clearFilters(activity: Activity) {
            setFilters(activity, "search", "")
            setFilters(activity, "stateName", "")
            setFilters(activity, "stateUF", "")
            setFilters(activity, "category", "")
            setFilters(activity, "region", "")
            setFilters(activity, "ddd", "")
            setFilters(activity, "valueMin", "")
            setFilters(activity, "valueMax", "")
        }
    }

}