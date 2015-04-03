begin_unit
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MultiDocValues
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SortedDocValues
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|MatchNoDocsQuery
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|Locale
import|;
end_import
begin_comment
comment|/**  * Utility for query time joining using TermsQuery and TermsCollector.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|JoinUtil
specifier|public
specifier|final
class|class
name|JoinUtil
block|{
comment|// No instances allowed
DECL|method|JoinUtil
specifier|private
name|JoinUtil
parameter_list|()
block|{   }
comment|/**    * Method for query time joining.    *<p>    * Execute the returned query with a {@link IndexSearcher} to retrieve all documents that have the same terms in the    * to field that match with documents matching the specified fromQuery and have the same terms in the from field.    *<p>    * In the case a single document relates to more than one document the<code>multipleValuesPerDocument</code> option    * should be set to true. When the<code>multipleValuesPerDocument</code> is set to<code>true</code> only the    * the score from the first encountered join value originating from the 'from' side is mapped into the 'to' side.    * Even in the case when a second join value related to a specific document yields a higher score. Obviously this    * doesn't apply in the case that {@link ScoreMode#None} is used, since no scores are computed at all.    *<p>    * Memory considerations: During joining all unique join values are kept in memory. On top of that when the scoreMode    * isn't set to {@link ScoreMode#None} a float value per unique join value is kept in memory for computing scores.    * When scoreMode is set to {@link ScoreMode#Avg} also an additional integer value is kept in memory per unique    * join value.    *    * @param fromField                 The from field to join from    * @param multipleValuesPerDocument Whether the from field has multiple terms per document    * @param toField                   The to field to join to    * @param fromQuery                 The query to match documents on the from side    * @param fromSearcher              The searcher that executed the specified fromQuery    * @param scoreMode                 Instructs how scores from the fromQuery are mapped to the returned query    * @return a {@link Query} instance that can be used to join documents based on the    *         terms in the from and to field    * @throws IOException If I/O related errors occur    */
DECL|method|createJoinQuery
specifier|public
specifier|static
name|Query
name|createJoinQuery
parameter_list|(
name|String
name|fromField
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|,
name|String
name|toField
parameter_list|,
name|Query
name|fromQuery
parameter_list|,
name|IndexSearcher
name|fromSearcher
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|None
case|:
name|TermsCollector
name|termsCollector
init|=
name|TermsCollector
operator|.
name|create
argument_list|(
name|fromField
argument_list|,
name|multipleValuesPerDocument
argument_list|)
decl_stmt|;
name|fromSearcher
operator|.
name|search
argument_list|(
name|fromQuery
argument_list|,
name|termsCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|TermsQuery
argument_list|(
name|toField
argument_list|,
name|fromQuery
argument_list|,
name|termsCollector
operator|.
name|getCollectorTerms
argument_list|()
argument_list|)
return|;
case|case
name|Total
case|:
case|case
name|Max
case|:
case|case
name|Avg
case|:
name|TermsWithScoreCollector
name|termsWithScoreCollector
init|=
name|TermsWithScoreCollector
operator|.
name|create
argument_list|(
name|fromField
argument_list|,
name|multipleValuesPerDocument
argument_list|,
name|scoreMode
argument_list|)
decl_stmt|;
name|fromSearcher
operator|.
name|search
argument_list|(
name|fromQuery
argument_list|,
name|termsWithScoreCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|TermsIncludingScoreQuery
argument_list|(
name|toField
argument_list|,
name|multipleValuesPerDocument
argument_list|,
name|termsWithScoreCollector
operator|.
name|getCollectedTerms
argument_list|()
argument_list|,
name|termsWithScoreCollector
operator|.
name|getScoresPerTerm
argument_list|()
argument_list|,
name|fromQuery
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Score mode %s isn't supported."
argument_list|,
name|scoreMode
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * A query time join using global ordinals over a dedicated join field.    *    * This join has certain restrictions and requirements:    * 1) A document can only refer to one other document. (but can be referred by one or more documents)    * 2) Documents on each side of the join must be distinguishable. Typically this can be done by adding an extra field    *    that identifies the "from" and "to" side and then the fromQuery and toQuery must take the this into account.    * 3) There must be a single sorted doc values join field used by both the "from" and "to" documents. This join field    *    should store the join values as UTF-8 strings.    * 4) An ordinal map must be provided that is created on top of the join field.    *    * @param joinField   The {@link org.apache.lucene.index.SortedDocValues} field containing the join values    * @param fromQuery   The query containing the actual user query. Also the fromQuery can only match "from" documents.    * @param toQuery     The query identifying all documents on the "to" side.    * @param searcher    The index searcher used to execute the from query    * @param scoreMode   Instructs how scores from the fromQuery are mapped to the returned query    * @param ordinalMap  The ordinal map constructed over the joinField. In case of a single segment index, no ordinal map    *                    needs to be provided.    * @return a {@link Query} instance that can be used to join documents based on the join field    * @throws IOException If I/O related errors occur    */
DECL|method|createJoinQuery
specifier|public
specifier|static
name|Query
name|createJoinQuery
parameter_list|(
name|String
name|joinField
parameter_list|,
name|Query
name|fromQuery
parameter_list|,
name|Query
name|toQuery
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|,
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordinalMap
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|int
name|numSegments
init|=
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|long
name|valueCount
decl_stmt|;
if|if
condition|(
name|numSegments
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|numSegments
operator|==
literal|1
condition|)
block|{
comment|// No need to use the ordinal map, because there is just one segment.
name|ordinalMap
operator|=
literal|null
expr_stmt|;
name|LeafReader
name|leafReader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|SortedDocValues
name|joinSortedDocValues
init|=
name|leafReader
operator|.
name|getSortedDocValues
argument_list|(
name|joinField
argument_list|)
decl_stmt|;
if|if
condition|(
name|joinSortedDocValues
operator|!=
literal|null
condition|)
block|{
name|valueCount
operator|=
name|joinSortedDocValues
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|ordinalMap
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"OrdinalMap is required, because there is more than 1 segment"
argument_list|)
throw|;
block|}
name|valueCount
operator|=
name|ordinalMap
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
block|}
name|Query
name|rewrittenFromQuery
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|fromQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|None
condition|)
block|{
name|GlobalOrdinalsCollector
name|globalOrdinalsCollector
init|=
operator|new
name|GlobalOrdinalsCollector
argument_list|(
name|joinField
argument_list|,
name|ordinalMap
argument_list|,
name|valueCount
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|fromQuery
argument_list|,
name|globalOrdinalsCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|GlobalOrdinalsQuery
argument_list|(
name|globalOrdinalsCollector
operator|.
name|getCollectorOrdinals
argument_list|()
argument_list|,
name|joinField
argument_list|,
name|ordinalMap
argument_list|,
name|toQuery
argument_list|,
name|rewrittenFromQuery
argument_list|,
name|indexReader
argument_list|)
return|;
block|}
name|GlobalOrdinalsWithScoreCollector
name|globalOrdinalsWithScoreCollector
decl_stmt|;
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|Total
case|:
name|globalOrdinalsWithScoreCollector
operator|=
operator|new
name|GlobalOrdinalsWithScoreCollector
operator|.
name|Sum
argument_list|(
name|joinField
argument_list|,
name|ordinalMap
argument_list|,
name|valueCount
argument_list|)
expr_stmt|;
break|break;
case|case
name|Max
case|:
name|globalOrdinalsWithScoreCollector
operator|=
operator|new
name|GlobalOrdinalsWithScoreCollector
operator|.
name|Max
argument_list|(
name|joinField
argument_list|,
name|ordinalMap
argument_list|,
name|valueCount
argument_list|)
expr_stmt|;
break|break;
case|case
name|Avg
case|:
name|globalOrdinalsWithScoreCollector
operator|=
operator|new
name|GlobalOrdinalsWithScoreCollector
operator|.
name|Avg
argument_list|(
name|joinField
argument_list|,
name|ordinalMap
argument_list|,
name|valueCount
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Score mode %s isn't supported."
argument_list|,
name|scoreMode
argument_list|)
argument_list|)
throw|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|fromQuery
argument_list|,
name|globalOrdinalsWithScoreCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|GlobalOrdinalsWithScoreQuery
argument_list|(
name|globalOrdinalsWithScoreCollector
argument_list|,
name|joinField
argument_list|,
name|ordinalMap
argument_list|,
name|toQuery
argument_list|,
name|rewrittenFromQuery
argument_list|,
name|indexReader
argument_list|)
return|;
block|}
block|}
end_class
end_unit
