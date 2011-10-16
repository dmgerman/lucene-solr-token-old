begin_unit
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|AddUpdateCommand
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  * Identifies the language of a set of input fields.   * Also supports mapping of field names based  * on detected language.   *<p>  * See<a href="http://wiki.apache.org/solr/LanguageDetection">http://wiki.apache.org/solr/LanguageDetection</a>  * @since 3.5  * @lucene.experimental  */
end_comment
begin_class
DECL|class|LanguageIdentifierUpdateProcessor
specifier|public
specifier|abstract
class|class
name|LanguageIdentifierUpdateProcessor
extends|extends
name|UpdateRequestProcessor
implements|implements
name|LangIdParams
block|{
DECL|field|log
specifier|protected
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LanguageIdentifierUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|enabled
specifier|protected
name|boolean
name|enabled
decl_stmt|;
DECL|field|inputFields
specifier|protected
name|String
index|[]
name|inputFields
init|=
block|{}
decl_stmt|;
DECL|field|mapFields
specifier|protected
name|String
index|[]
name|mapFields
init|=
block|{}
decl_stmt|;
DECL|field|mapPattern
specifier|protected
name|Pattern
name|mapPattern
decl_stmt|;
DECL|field|mapReplaceStr
specifier|protected
name|String
name|mapReplaceStr
decl_stmt|;
DECL|field|langField
specifier|protected
name|String
name|langField
decl_stmt|;
DECL|field|langsField
specifier|protected
name|String
name|langsField
decl_stmt|;
comment|// MultiValued, contains all languages detected
DECL|field|docIdField
specifier|protected
name|String
name|docIdField
decl_stmt|;
DECL|field|fallbackValue
specifier|protected
name|String
name|fallbackValue
decl_stmt|;
DECL|field|fallbackFields
specifier|protected
name|String
index|[]
name|fallbackFields
init|=
block|{}
decl_stmt|;
DECL|field|enableMapping
specifier|protected
name|boolean
name|enableMapping
decl_stmt|;
DECL|field|mapKeepOrig
specifier|protected
name|boolean
name|mapKeepOrig
decl_stmt|;
DECL|field|overwrite
specifier|protected
name|boolean
name|overwrite
decl_stmt|;
DECL|field|mapOverwrite
specifier|protected
name|boolean
name|mapOverwrite
decl_stmt|;
DECL|field|mapIndividual
specifier|protected
name|boolean
name|mapIndividual
decl_stmt|;
DECL|field|enforceSchema
specifier|protected
name|boolean
name|enforceSchema
decl_stmt|;
DECL|field|threshold
specifier|protected
name|double
name|threshold
decl_stmt|;
DECL|field|langWhitelist
specifier|protected
name|HashSet
argument_list|<
name|String
argument_list|>
name|langWhitelist
decl_stmt|;
DECL|field|mapIndividualFieldsSet
specifier|protected
name|HashSet
argument_list|<
name|String
argument_list|>
name|mapIndividualFieldsSet
decl_stmt|;
DECL|field|allMapFieldsSet
specifier|protected
name|HashSet
argument_list|<
name|String
argument_list|>
name|allMapFieldsSet
decl_stmt|;
DECL|field|lcMap
specifier|protected
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|lcMap
decl_stmt|;
DECL|field|schema
specifier|protected
name|IndexSchema
name|schema
decl_stmt|;
comment|// Regex patterns
DECL|field|tikaSimilarityPattern
specifier|protected
specifier|final
name|Pattern
name|tikaSimilarityPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*\\((.*?)\\)"
argument_list|)
decl_stmt|;
DECL|field|langPattern
specifier|protected
specifier|final
name|Pattern
name|langPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\{lang\\}"
argument_list|)
decl_stmt|;
DECL|method|LanguageIdentifierUpdateProcessor
specifier|public
name|LanguageIdentifierUpdateProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|schema
operator|=
name|req
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|initParams
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initParams
specifier|private
name|void
name|initParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
comment|// Document-centric langId params
name|setEnabled
argument_list|(
name|params
operator|.
name|getBool
argument_list|(
name|LANGUAGE_ID
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|FIELDS_PARAM
argument_list|,
literal|""
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|inputFields
operator|=
name|params
operator|.
name|get
argument_list|(
name|FIELDS_PARAM
argument_list|,
literal|""
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|langField
operator|=
name|params
operator|.
name|get
argument_list|(
name|LANG_FIELD
argument_list|,
name|DOCID_LANGFIELD_DEFAULT
argument_list|)
expr_stmt|;
name|langsField
operator|=
name|params
operator|.
name|get
argument_list|(
name|LANGS_FIELD
argument_list|,
name|DOCID_LANGSFIELD_DEFAULT
argument_list|)
expr_stmt|;
name|docIdField
operator|=
name|params
operator|.
name|get
argument_list|(
name|DOCID_PARAM
argument_list|,
name|DOCID_FIELD_DEFAULT
argument_list|)
expr_stmt|;
name|fallbackValue
operator|=
name|params
operator|.
name|get
argument_list|(
name|FALLBACK
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|FALLBACK_FIELDS
argument_list|,
literal|""
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fallbackFields
operator|=
name|params
operator|.
name|get
argument_list|(
name|FALLBACK_FIELDS
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|overwrite
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|OVERWRITE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|langWhitelist
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|threshold
operator|=
name|params
operator|.
name|getDouble
argument_list|(
name|THRESHOLD
argument_list|,
name|DOCID_THRESHOLD_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|LANG_WHITELIST
argument_list|,
literal|""
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|lang
range|:
name|params
operator|.
name|get
argument_list|(
name|LANG_WHITELIST
argument_list|,
literal|""
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|langWhitelist
operator|.
name|add
argument_list|(
name|lang
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Mapping params (field centric)
name|enableMapping
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|MAP_ENABLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|MAP_FL
argument_list|,
literal|""
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|mapFields
operator|=
name|params
operator|.
name|get
argument_list|(
name|MAP_FL
argument_list|,
literal|""
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapFields
operator|=
name|inputFields
expr_stmt|;
block|}
name|mapKeepOrig
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|MAP_KEEP_ORIG
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|mapOverwrite
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|MAP_OVERWRITE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|mapIndividual
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|MAP_INDIVIDUAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Process individual fields
name|String
index|[]
name|mapIndividualFields
init|=
block|{}
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|MAP_INDIVIDUAL_FL
argument_list|,
literal|""
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|mapIndividualFields
operator|=
name|params
operator|.
name|get
argument_list|(
name|MAP_INDIVIDUAL_FL
argument_list|,
literal|""
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapIndividualFields
operator|=
name|mapFields
expr_stmt|;
block|}
name|mapIndividualFieldsSet
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mapIndividualFields
argument_list|)
argument_list|)
expr_stmt|;
comment|// Compile a union of the lists of fields to map
name|allMapFieldsSet
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mapFields
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|mapFields
argument_list|,
name|mapIndividualFields
argument_list|)
condition|)
block|{
name|allMapFieldsSet
operator|.
name|addAll
argument_list|(
name|mapIndividualFieldsSet
argument_list|)
expr_stmt|;
block|}
comment|// Language Code mapping
name|lcMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|MAP_LCMAP
argument_list|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|mapping
range|:
name|params
operator|.
name|get
argument_list|(
name|MAP_LCMAP
argument_list|)
operator|.
name|split
argument_list|(
literal|"[, ]"
argument_list|)
control|)
block|{
name|String
index|[]
name|keyVal
init|=
name|mapping
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyVal
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|lcMap
operator|.
name|put
argument_list|(
name|keyVal
index|[
literal|0
index|]
argument_list|,
name|keyVal
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unsupported format for langid.map.lcmap: "
operator|+
name|mapping
operator|+
literal|". Skipping this mapping."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|enforceSchema
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|ENFORCE_SCHEMA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mapPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|MAP_PATTERN
argument_list|,
name|MAP_PATTERN_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|mapReplaceStr
operator|=
name|params
operator|.
name|get
argument_list|(
name|MAP_REPLACE
argument_list|,
name|MAP_REPLACE_DEFAULT
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"LangId configured"
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputFields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Missing or faulty configuration of LanguageIdentifierUpdateProcessor. Input fields must be specified as a comma separated list"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isEnabled
argument_list|()
condition|)
block|{
name|process
argument_list|(
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Processor not enabled, not running"
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is the main, testable process method called from processAdd()    * @param doc the SolrInputDocument to work on    * @return the modified SolrInputDocument    */
DECL|method|process
specifier|protected
name|SolrInputDocument
name|process
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
name|String
name|docLang
init|=
literal|null
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|docLangs
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|fallbackLang
init|=
name|getFallbackLang
argument_list|(
name|doc
argument_list|,
name|fallbackFields
argument_list|,
name|fallbackValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|langField
operator|==
literal|null
operator|||
operator|!
name|doc
operator|.
name|containsKey
argument_list|(
name|langField
argument_list|)
operator|||
operator|(
name|doc
operator|.
name|containsKey
argument_list|(
name|langField
argument_list|)
operator|&&
name|overwrite
operator|)
condition|)
block|{
name|String
name|allText
init|=
name|concatFields
argument_list|(
name|doc
argument_list|,
name|inputFields
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DetectedLanguage
argument_list|>
name|languagelist
init|=
name|detectLanguage
argument_list|(
name|allText
argument_list|)
decl_stmt|;
name|docLang
operator|=
name|resolveLanguage
argument_list|(
name|languagelist
argument_list|,
name|fallbackLang
argument_list|)
expr_stmt|;
name|docLangs
operator|.
name|add
argument_list|(
name|docLang
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Detected main document language from fields "
operator|+
name|inputFields
operator|+
literal|": "
operator|+
name|docLang
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|containsKey
argument_list|(
name|langField
argument_list|)
operator|&&
name|overwrite
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Overwritten old value "
operator|+
name|doc
operator|.
name|getFieldValue
argument_list|(
name|langField
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|langField
operator|!=
literal|null
operator|&&
name|langField
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|langField
argument_list|,
name|docLang
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// langField is set, we sanity check it against whitelist and fallback
name|docLang
operator|=
name|resolveLanguage
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
name|langField
argument_list|)
argument_list|,
name|fallbackLang
argument_list|)
expr_stmt|;
name|docLangs
operator|.
name|add
argument_list|(
name|docLang
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Field "
operator|+
name|langField
operator|+
literal|" already contained value "
operator|+
name|docLang
operator|+
literal|", not overwriting."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|enableMapping
condition|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|allMapFieldsSet
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
name|fieldLang
decl_stmt|;
if|if
condition|(
name|mapIndividual
operator|&&
name|mapIndividualFieldsSet
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
name|text
init|=
operator|(
name|String
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DetectedLanguage
argument_list|>
name|languagelist
init|=
name|detectLanguage
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|fieldLang
operator|=
name|resolveLanguage
argument_list|(
name|languagelist
argument_list|,
name|docLang
argument_list|)
expr_stmt|;
name|docLangs
operator|.
name|add
argument_list|(
name|fieldLang
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Mapping field "
operator|+
name|fieldName
operator|+
literal|" using individually detected language "
operator|+
name|fieldLang
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldLang
operator|=
name|docLang
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Mapping field "
operator|+
name|fieldName
operator|+
literal|" using document global language "
operator|+
name|fieldLang
argument_list|)
expr_stmt|;
block|}
name|String
name|mappedOutputField
init|=
name|getMappedField
argument_list|(
name|fieldName
argument_list|,
name|fieldLang
argument_list|)
decl_stmt|;
if|if
condition|(
name|enforceSchema
operator|&&
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unsuccessful field name mapping to {}, field does not exist, skipping mapping."
argument_list|,
name|mappedOutputField
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|mappedOutputField
operator|=
name|fieldName
expr_stmt|;
block|}
if|if
condition|(
name|mappedOutputField
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Mapping field {} to {}"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
name|docIdField
argument_list|)
argument_list|,
name|fieldLang
argument_list|)
expr_stmt|;
name|SolrInputField
name|inField
init|=
name|doc
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|mappedOutputField
argument_list|,
name|inField
operator|.
name|getValue
argument_list|()
argument_list|,
name|inField
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|mapKeepOrig
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Removing old field {}"
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid output field mapping for "
operator|+
name|fieldName
operator|+
literal|" field and language: "
operator|+
name|fieldLang
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Document {} does not contain input field {}. Skipping this field."
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
name|docIdField
argument_list|)
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Set the languages field to an array of all detected languages
if|if
condition|(
name|langsField
operator|!=
literal|null
operator|&&
name|langsField
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|langsField
argument_list|,
name|docLangs
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
comment|/**    * Decides the fallback language, either from content of fallback field or fallback value    * @param doc the Solr document    * @param fallbackFields an array of strings with field names containing fallback language codes    * @param fallbackValue a language code to use in case no fallbackFields are found    */
DECL|method|getFallbackLang
specifier|private
name|String
name|getFallbackLang
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|String
index|[]
name|fallbackFields
parameter_list|,
name|String
name|fallbackValue
parameter_list|)
block|{
name|String
name|lang
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fallbackFields
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|lang
operator|=
operator|(
name|String
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Language fallback to field "
operator|+
name|field
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Language fallback to value "
operator|+
name|fallbackValue
argument_list|)
expr_stmt|;
name|lang
operator|=
name|fallbackValue
expr_stmt|;
block|}
return|return
name|lang
return|;
block|}
comment|/*    * Concatenates content from multiple fields    */
DECL|method|concatFields
specifier|protected
name|String
name|concatFields
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|String
index|[]
name|fields
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|inputFields
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Appending field "
operator|+
name|fieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|Object
name|content
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
operator|instanceof
name|String
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Field "
operator|+
name|fieldName
operator|+
literal|" not a String value, not including in detection"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Detects language(s) from a string.    * Classes wishing to implement their own language detection module should override this method.    * @param content The content to identify    * @return List of detected language(s) according to RFC-3066    */
DECL|method|detectLanguage
specifier|protected
specifier|abstract
name|List
argument_list|<
name|DetectedLanguage
argument_list|>
name|detectLanguage
parameter_list|(
name|String
name|content
parameter_list|)
function_decl|;
comment|/**    * Chooses a language based on the list of candidates detected     * @param language language code as a string    * @param fallbackLang the language code to use as a fallback    * @return a string of the chosen language    */
DECL|method|resolveLanguage
specifier|protected
name|String
name|resolveLanguage
parameter_list|(
name|String
name|language
parameter_list|,
name|String
name|fallbackLang
parameter_list|)
block|{
name|List
argument_list|<
name|DetectedLanguage
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|DetectedLanguage
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|DetectedLanguage
argument_list|(
name|language
argument_list|,
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|resolveLanguage
argument_list|(
name|l
argument_list|,
name|fallbackLang
argument_list|)
return|;
block|}
comment|/**    * Chooses a language based on the list of candidates detected     * @param languages a List of DetectedLanguages with certainty score    * @param fallbackLang the language code to use as a fallback    * @return a string of the chosen language    */
DECL|method|resolveLanguage
specifier|protected
name|String
name|resolveLanguage
parameter_list|(
name|List
argument_list|<
name|DetectedLanguage
argument_list|>
name|languages
parameter_list|,
name|String
name|fallbackLang
parameter_list|)
block|{
name|String
name|langStr
decl_stmt|;
if|if
condition|(
name|languages
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No language detected, using fallback {}"
argument_list|,
name|fallbackLang
argument_list|)
expr_stmt|;
name|langStr
operator|=
name|fallbackLang
expr_stmt|;
block|}
else|else
block|{
name|DetectedLanguage
name|lang
init|=
name|languages
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|langWhitelist
operator|.
name|isEmpty
argument_list|()
operator|||
name|langWhitelist
operator|.
name|contains
argument_list|(
name|lang
operator|.
name|getLangCode
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Language detected {} with certainty {}"
argument_list|,
name|lang
operator|.
name|getLangCode
argument_list|()
argument_list|,
name|lang
operator|.
name|getCertainty
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lang
operator|.
name|getCertainty
argument_list|()
operator|>=
name|threshold
condition|)
block|{
name|langStr
operator|=
name|lang
operator|.
name|getLangCode
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Detected language below threshold {}, using fallback {}"
argument_list|,
name|threshold
argument_list|,
name|fallbackLang
argument_list|)
expr_stmt|;
name|langStr
operator|=
name|fallbackLang
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Detected a language not in whitelist ({}), using fallback {}"
argument_list|,
name|lang
operator|.
name|getLangCode
argument_list|()
argument_list|,
name|fallbackLang
argument_list|)
expr_stmt|;
name|langStr
operator|=
name|fallbackLang
expr_stmt|;
block|}
block|}
if|if
condition|(
name|langStr
operator|==
literal|null
operator|||
name|langStr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Language resolved to null or empty string. Fallback not configured?"
argument_list|)
expr_stmt|;
name|langStr
operator|=
literal|""
expr_stmt|;
block|}
return|return
name|langStr
return|;
block|}
comment|/**    * Returns the name of the field to map the current contents into, so that they are properly analyzed.  For instance    * if the currentField is "text" and the code is "en", the new field would be "text_en".  If such a field doesn't exist,    * then null is returned.    *    * @param currentField The current field name    * @param language the language code    * @return The new schema field name, based on pattern and replace    */
DECL|method|getMappedField
specifier|protected
name|String
name|getMappedField
parameter_list|(
name|String
name|currentField
parameter_list|,
name|String
name|language
parameter_list|)
block|{
name|String
name|lc
init|=
name|lcMap
operator|.
name|containsKey
argument_list|(
name|language
argument_list|)
condition|?
name|lcMap
operator|.
name|get
argument_list|(
name|language
argument_list|)
else|:
name|language
decl_stmt|;
name|String
name|newFieldName
init|=
name|langPattern
operator|.
name|matcher
argument_list|(
name|mapPattern
operator|.
name|matcher
argument_list|(
name|currentField
argument_list|)
operator|.
name|replaceFirst
argument_list|(
name|mapReplaceStr
argument_list|)
argument_list|)
operator|.
name|replaceFirst
argument_list|(
name|lc
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Doing mapping from "
operator|+
name|currentField
operator|+
literal|" with language "
operator|+
name|language
operator|+
literal|" to field "
operator|+
name|newFieldName
argument_list|)
expr_stmt|;
return|return
name|newFieldName
return|;
block|}
comment|/**    * Tells if this processor is enabled or not    * @return true if enabled, else false    */
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
DECL|method|setEnabled
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
block|}
end_class
end_unit
