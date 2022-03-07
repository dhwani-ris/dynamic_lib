package com.dhwaniris.dynamicForm.db.dbhelper.form

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class Form(
        @SerializedName("_id") var _id: String? = null,
        @SerializedName("formId") var formId: Int = 0,
        @SerializedName("modifiedAt") var modifiedAt: String? = null,
        @SerializedName("createdAt") var createdAt: String? = null,
        @SerializedName("version") var version: String? = null,
        @SerializedName("fillCount") var fillCount: Int = 0,
        @SerializedName("formIcon") var formIcon: String? = null,
        @SerializedName("editable") var editable: Boolean = false,
        @SerializedName("isActive") var isActive: Boolean = false,
        @SerializedName("language", alternate = ["languages"]) var language: List<LanguageBean> = listOf(),
        @SerializedName("location") var location: Boolean = false,
        @SerializedName("delayLocation") var delayLocation: Boolean = false,
        @SerializedName("isMedia") var isMedia: Boolean = false,
        @SerializedName("expiryDate") var expiryDate: Long = 0,
//@SerializedName("minAppVersion")   private String minAppVersion; ,
//@SerializedName("minAppVersion")   private String minAppVersion; ,
//@SerializedName("minAppVersion")   private String minAppVersion;,
        @SerializedName("dynamicData") var dynamicData: Boolean = false,
        @SerializedName("externalResource") var externalResource: List<ExternalResourceBean>? = null,
//@SerializedName("project")   private ProjectNameBean project; ,
//@SerializedName("project")   private ProjectNameBean project; ,
//@SerializedName("project")   private ProjectNameBean project; ,
        @SerializedName("formSynced") var formSynced: Boolean = false,
        @SerializedName("dataUpdateStatus") var dataUpdateStatus: Boolean = false,
        @SerializedName("errorManagementStatus") var errorManagementStatus: Boolean = false,
        @SerializedName("dataCollectionStatus") var dataCollectionStatus: Boolean = false,
        @SerializedName("duplicateCheckQuestions") var duplicateCheckQuestions: List<String>? = null,
        @SerializedName("keyInfoOrders") var keyInfoOrders: List<String>? = null,
        @SerializedName("getDynamicOptionsList") var getDynamicOptionsList: List<GetDynamicOption>? = null,
        @SerializedName("createDynamicOptionList") var createDynamicOptionList: List<CreateDynamicOption>? = null,
        @SerializedName("isMasterSynced") var isMasterSynced: Boolean = false
) : Cloneable {
    fun isDataUpdateStatus(): Boolean = dataUpdateStatus
    fun isErrorManagementStatus(): Boolean = errorManagementStatus
    fun isDataCollectionStatus(): Boolean = dataCollectionStatus
    fun isFormSynced(): Boolean = formSynced
    fun isEditable(): Boolean = editable
    fun isIsActive(): Boolean = isActive
    fun setIsActive(isActive: Boolean) {
        this.isActive = isActive
    }

    fun isLocation(): Boolean = location
    fun isDynamicData(): Boolean = dynamicData

    fun getEmptyJson(): JSONObject {
        val json = JSONObject()
        language.forEach { jsonO ->
            jsonO.question.forEach { ques ->
                try {
                    json.put(ques.columnName, "")
                } catch (e: Exception) {
                }
            }
        }
        return json
    }

    fun getJsonColumnOrder(): JSONObject {
        val json = JSONObject()
        language.forEach {jsonO->
            jsonO.question.forEach {ques->
                try {
                    json.put(ques.columnName, ques.order)
                } catch (e: Exception) {
                    println("Col  = " + ques.columnName + "   Order = " + ques.order)
                    e.printStackTrace()
                }
            }
        }
        return json
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Form {
        return super.clone() as Form
    }
}