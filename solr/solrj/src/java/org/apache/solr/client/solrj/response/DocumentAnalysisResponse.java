begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package
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
name|util
operator|.
name|NamedList
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
name|Iterator
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
name|Map
import|;
end_import
begin_comment
comment|/**  * A response that is returned by processing the {@link org.apache.solr.client.solrj.request.DocumentAnalysisRequest}.  * Holds a map of {@link DocumentAnalysis} objects by a document id (unique key).  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|DocumentAnalysisResponse
specifier|public
class|class
name|DocumentAnalysisResponse
extends|extends
name|AnalysisResponseBase
implements|implements
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DocumentAnalysisResponse
operator|.
name|DocumentAnalysis
argument_list|>
argument_list|>
block|{
DECL|field|documentAnalysisByKey
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentAnalysis
argument_list|>
name|documentAnalysisByKey
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocumentAnalysis
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|setResponse
specifier|public
name|void
name|setResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
parameter_list|)
block|{
name|super
operator|.
name|setResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|analysis
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"analysis"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|document
range|:
name|analysis
control|)
block|{
name|DocumentAnalysis
name|documentAnalysis
init|=
operator|new
name|DocumentAnalysis
argument_list|(
name|document
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|fieldEntry
range|:
name|document
operator|.
name|getValue
argument_list|()
control|)
block|{
name|FieldAnalysis
name|fieldAnalysis
init|=
operator|new
name|FieldAnalysis
argument_list|(
name|fieldEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|field
init|=
name|fieldEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|query
init|=
operator|(
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|field
operator|.
name|get
argument_list|(
literal|"query"
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|phases
init|=
name|buildPhases
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|fieldAnalysis
operator|.
name|setQueryPhases
argument_list|(
name|phases
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|index
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|>
operator|)
name|field
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|valueEntry
range|:
name|index
control|)
block|{
name|String
name|fieldValue
init|=
name|valueEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|valueNL
init|=
name|valueEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|phases
init|=
name|buildPhases
argument_list|(
name|valueNL
argument_list|)
decl_stmt|;
name|fieldAnalysis
operator|.
name|setIndexPhases
argument_list|(
name|fieldValue
argument_list|,
name|phases
argument_list|)
expr_stmt|;
block|}
name|documentAnalysis
operator|.
name|addFieldAnalysis
argument_list|(
name|fieldAnalysis
argument_list|)
expr_stmt|;
block|}
name|documentAnalysisByKey
operator|.
name|put
argument_list|(
name|documentAnalysis
operator|.
name|getDocumentKey
argument_list|()
argument_list|,
name|documentAnalysis
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the number of document analyses in this response.    *    * @return The number of document analyses in this response.    */
DECL|method|getDocumentAnalysesCount
specifier|public
name|int
name|getDocumentAnalysesCount
parameter_list|()
block|{
return|return
name|documentAnalysisByKey
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns the document analysis for the document associated with the given unique key (id), {@code null} if no such    * association exists.    *    * @param documentKey The document unique key.    *    * @return The document analysis for the document associated with the given unique key (id).    */
DECL|method|getDocumentAnalysis
specifier|public
name|DocumentAnalysis
name|getDocumentAnalysis
parameter_list|(
name|String
name|documentKey
parameter_list|)
block|{
return|return
name|documentAnalysisByKey
operator|.
name|get
argument_list|(
name|documentKey
argument_list|)
return|;
block|}
comment|/**    * Returns an iterator over the document analyses map.    *    * @return An iterator over the document analyses map.    */
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DocumentAnalysis
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|documentAnalysisByKey
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|//================================================= Inner Classes ==================================================
comment|/**    * An analysis process breakdown of a document. Holds a map of field analyses by the field name.    */
DECL|class|DocumentAnalysis
specifier|public
specifier|static
class|class
name|DocumentAnalysis
implements|implements
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FieldAnalysis
argument_list|>
argument_list|>
block|{
DECL|field|documentKey
specifier|private
specifier|final
name|String
name|documentKey
decl_stmt|;
DECL|field|fieldAnalysisByFieldName
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FieldAnalysis
argument_list|>
name|fieldAnalysisByFieldName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldAnalysis
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|DocumentAnalysis
specifier|private
name|DocumentAnalysis
parameter_list|(
name|String
name|documentKey
parameter_list|)
block|{
name|this
operator|.
name|documentKey
operator|=
name|documentKey
expr_stmt|;
block|}
DECL|method|addFieldAnalysis
specifier|private
name|void
name|addFieldAnalysis
parameter_list|(
name|FieldAnalysis
name|fieldAnalysis
parameter_list|)
block|{
name|fieldAnalysisByFieldName
operator|.
name|put
argument_list|(
name|fieldAnalysis
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|fieldAnalysis
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the unique key of the analyzed document.      *      * @return The unique key of the analyzed document.      */
DECL|method|getDocumentKey
specifier|public
name|String
name|getDocumentKey
parameter_list|()
block|{
return|return
name|documentKey
return|;
block|}
comment|/**      * Returns the number of field analyses for the documents.      *      * @return The number of field analyses for the documents.      */
DECL|method|getFieldAnalysesCount
specifier|public
name|int
name|getFieldAnalysesCount
parameter_list|()
block|{
return|return
name|fieldAnalysisByFieldName
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getFieldAnalysis
specifier|public
name|FieldAnalysis
name|getFieldAnalysis
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|fieldAnalysisByFieldName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator over the field analyses map.      *      * @return An iterator over the field analyses map.      */
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FieldAnalysis
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fieldAnalysisByFieldName
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
comment|/**    * An analysis process breakdown for a specific field. Holds a list of query time analysis phases (that is, if a    * query analysis was requested in the first place) and a list of index time analysis phases for each field value (a    * field can be multi-valued).    */
DECL|class|FieldAnalysis
specifier|public
specifier|static
class|class
name|FieldAnalysis
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|queryPhases
specifier|private
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|queryPhases
decl_stmt|;
DECL|field|indexPhasesByFieldValue
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
argument_list|>
name|indexPhasesByFieldValue
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FieldAnalysis
specifier|private
name|FieldAnalysis
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
DECL|method|setQueryPhases
specifier|public
name|void
name|setQueryPhases
parameter_list|(
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|queryPhases
parameter_list|)
block|{
name|this
operator|.
name|queryPhases
operator|=
name|queryPhases
expr_stmt|;
block|}
DECL|method|setIndexPhases
specifier|public
name|void
name|setIndexPhases
parameter_list|(
name|String
name|fieldValue
parameter_list|,
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|indexPhases
parameter_list|)
block|{
name|indexPhasesByFieldValue
operator|.
name|put
argument_list|(
name|fieldValue
argument_list|,
name|indexPhases
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the field name.      *      * @return The name of the field.      */
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
comment|/**      * Returns the number of query time analysis phases or {@code -1} if       * this field analysis doesn't hold a query time analysis.      *      * @return Returns the number of query time analysis phases or {@code -1}       *         if this field analysis doesn't hold a query time analysis.      */
DECL|method|getQueryPhasesCount
specifier|public
name|int
name|getQueryPhasesCount
parameter_list|()
block|{
return|return
name|queryPhases
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|queryPhases
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Returns the query time analysis phases for the field or {@code null}       * if this field doesn't hold a query time analysis.      *      * @return Returns the query time analysis phases for the field or       *         {@code null} if this field doesn't hold a query time analysis.      */
DECL|method|getQueryPhases
specifier|public
name|Iterable
argument_list|<
name|AnalysisPhase
argument_list|>
name|getQueryPhases
parameter_list|()
block|{
return|return
name|queryPhases
return|;
block|}
comment|/**      * Returns the number of values the field has.      *      * @return The number of values the field has.      */
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|indexPhasesByFieldValue
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Returns the number of index time analysis phases the given field value has.      *      * @param fieldValue The field value.      *      * @return The number of index time analysis phases the given field value has.      */
DECL|method|getIndexPhasesCount
specifier|public
name|int
name|getIndexPhasesCount
parameter_list|(
name|String
name|fieldValue
parameter_list|)
block|{
return|return
name|indexPhasesByFieldValue
operator|.
name|get
argument_list|(
name|fieldValue
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Returns the index time analysis phases for the given field value.      *      * @param fieldValue The field value.      *      * @return The index time analysis phases for the given field value.      */
DECL|method|getIndexPhases
specifier|public
name|Iterable
argument_list|<
name|AnalysisPhase
argument_list|>
name|getIndexPhases
parameter_list|(
name|String
name|fieldValue
parameter_list|)
block|{
return|return
name|indexPhasesByFieldValue
operator|.
name|get
argument_list|(
name|fieldValue
argument_list|)
return|;
block|}
comment|/**      * Returns the index time analysis phases for all field values.      *      * @return Returns the index time analysis phases for all field value.      */
DECL|method|getIndexPhasesByFieldValue
specifier|public
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
argument_list|>
argument_list|>
name|getIndexPhasesByFieldValue
parameter_list|()
block|{
return|return
name|indexPhasesByFieldValue
operator|.
name|entrySet
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
