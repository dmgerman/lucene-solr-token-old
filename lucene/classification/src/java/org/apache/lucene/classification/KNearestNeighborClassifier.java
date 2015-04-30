begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
package|;
end_package
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
name|io
operator|.
name|StringReader
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
name|Collections
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|StorableField
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
name|Term
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
name|queries
operator|.
name|mlt
operator|.
name|MoreLikeThis
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|ScoreDoc
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
name|TopDocs
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
name|WildcardQuery
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
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * A k-Nearest Neighbor classifier (see<code>http://en.wikipedia.org/wiki/K-nearest_neighbors</code>) based  * on {@link MoreLikeThis}  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|KNearestNeighborClassifier
specifier|public
class|class
name|KNearestNeighborClassifier
implements|implements
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|mlt
specifier|private
specifier|final
name|MoreLikeThis
name|mlt
decl_stmt|;
DECL|field|textFieldNames
specifier|private
specifier|final
name|String
index|[]
name|textFieldNames
decl_stmt|;
DECL|field|classFieldName
specifier|private
specifier|final
name|String
name|classFieldName
decl_stmt|;
DECL|field|indexSearcher
specifier|private
specifier|final
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|field|k
specifier|private
specifier|final
name|int
name|k
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|method|KNearestNeighborClassifier
specifier|public
name|KNearestNeighborClassifier
parameter_list|(
name|LeafReader
name|leafReader
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|k
parameter_list|,
name|int
name|minDocsFreq
parameter_list|,
name|int
name|minTermFreq
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|String
modifier|...
name|textFieldNames
parameter_list|)
block|{
name|this
operator|.
name|textFieldNames
operator|=
name|textFieldNames
expr_stmt|;
name|this
operator|.
name|classFieldName
operator|=
name|classFieldName
expr_stmt|;
name|this
operator|.
name|mlt
operator|=
operator|new
name|MoreLikeThis
argument_list|(
name|leafReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|textFieldNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|leafReader
argument_list|)
expr_stmt|;
if|if
condition|(
name|minDocsFreq
operator|>
literal|0
condition|)
block|{
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|minDocsFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minTermFreq
operator|>
literal|0
condition|)
block|{
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
name|minTermFreq
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|k
operator|=
name|k
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|assignClass
specifier|public
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|assignClass
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|topDocs
init|=
name|knnSearch
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|doclist
init|=
name|buildListFromTopDocs
argument_list|(
name|topDocs
argument_list|)
decl_stmt|;
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|retval
init|=
literal|null
decl_stmt|;
name|double
name|maxscore
init|=
operator|-
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|element
range|:
name|doclist
control|)
block|{
if|if
condition|(
name|element
operator|.
name|getScore
argument_list|()
operator|>
name|maxscore
condition|)
block|{
name|retval
operator|=
name|element
expr_stmt|;
name|maxscore
operator|=
name|element
operator|.
name|getScore
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|retval
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getClasses
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|topDocs
init|=
name|knnSearch
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|doclist
init|=
name|buildListFromTopDocs
argument_list|(
name|topDocs
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|doclist
argument_list|)
expr_stmt|;
return|return
name|doclist
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getClasses
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|topDocs
init|=
name|knnSearch
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|doclist
init|=
name|buildListFromTopDocs
argument_list|(
name|topDocs
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|doclist
argument_list|)
expr_stmt|;
return|return
name|doclist
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
return|;
block|}
DECL|method|knnSearch
specifier|private
name|TopDocs
name|knnSearch
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|mltQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|textFieldName
range|:
name|textFieldNames
control|)
block|{
name|mltQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|mlt
operator|.
name|like
argument_list|(
name|textFieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Query
name|classFieldQuery
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
literal|"*"
argument_list|)
argument_list|)
decl_stmt|;
name|mltQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|classFieldQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|mltQuery
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|indexSearcher
operator|.
name|search
argument_list|(
name|mltQuery
argument_list|,
name|k
argument_list|)
return|;
block|}
DECL|method|buildListFromTopDocs
specifier|private
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|buildListFromTopDocs
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|classCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|topDocs
operator|.
name|scoreDocs
control|)
block|{
name|StorableField
name|storableField
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
name|classFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|storableField
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|cl
init|=
operator|new
name|BytesRef
argument_list|(
name|storableField
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
name|count
init|=
name|classCounts
operator|.
name|get
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|classCounts
operator|.
name|put
argument_list|(
name|cl
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|classCounts
operator|.
name|put
argument_list|(
name|cl
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|returnList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|sumdoc
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|classCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Integer
name|count
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|returnList
operator|.
name|add
argument_list|(
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|clone
argument_list|()
argument_list|,
name|count
operator|/
operator|(
name|double
operator|)
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|sumdoc
operator|+=
name|count
expr_stmt|;
block|}
comment|//correction
if|if
condition|(
name|sumdoc
operator|<
name|k
condition|)
block|{
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|cr
range|:
name|returnList
control|)
block|{
name|cr
operator|.
name|setScore
argument_list|(
name|cr
operator|.
name|getScore
argument_list|()
operator|*
operator|(
name|double
operator|)
name|k
operator|/
operator|(
name|double
operator|)
name|sumdoc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|returnList
return|;
block|}
block|}
end_class
end_unit
