begin_unit
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.xml.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|builders
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
name|Set
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
name|analysis
operator|.
name|TokenStream
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|MoreLikeThisQuery
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
name|queryparser
operator|.
name|xml
operator|.
name|QueryBuilder
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
name|queryparser
operator|.
name|xml
operator|.
name|DOMUtils
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
name|queryparser
operator|.
name|xml
operator|.
name|ParserException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Builder for {@link MoreLikeThisQuery}  */
end_comment
begin_class
DECL|class|LikeThisQueryBuilder
specifier|public
class|class
name|LikeThisQueryBuilder
implements|implements
name|QueryBuilder
block|{
DECL|field|DEFAULT_MAX_QUERY_TERMS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_QUERY_TERMS
init|=
literal|20
decl_stmt|;
DECL|field|DEFAULT_MIN_TERM_FREQUENCY
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_TERM_FREQUENCY
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_PERCENT_TERMS_TO_MATCH
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_PERCENT_TERMS_TO_MATCH
init|=
literal|30
decl_stmt|;
comment|//default is a 3rd of selected terms must match
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|defaultFieldNames
specifier|private
specifier|final
name|String
name|defaultFieldNames
index|[]
decl_stmt|;
DECL|method|LikeThisQueryBuilder
specifier|public
name|LikeThisQueryBuilder
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
index|[]
name|defaultFieldNames
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|defaultFieldNames
operator|=
name|defaultFieldNames
expr_stmt|;
block|}
comment|/* (non-Javadoc)     * @see org.apache.lucene.xmlparser.QueryObjectBuilder#process(org.w3c.dom.Element)     */
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|fieldsList
init|=
name|e
operator|.
name|getAttribute
argument_list|(
literal|"fieldNames"
argument_list|)
decl_stmt|;
comment|//a comma-delimited list of fields
name|String
name|fields
index|[]
init|=
name|defaultFieldNames
decl_stmt|;
if|if
condition|(
operator|(
name|fieldsList
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|fieldsList
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|fields
operator|=
name|fieldsList
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
comment|//trim the fieldnames
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
block|}
comment|//Parse any "stopWords" attribute
comment|//TODO MoreLikeThis needs to ideally have per-field stopWords lists - until then
comment|//I use all analyzers/fields to generate multi-field compatible stop list
name|String
name|stopWords
init|=
name|e
operator|.
name|getAttribute
argument_list|(
literal|"stopWords"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|stopWordsSet
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|stopWords
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|fields
operator|!=
literal|null
operator|)
condition|)
block|{
name|stopWordsSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
try|try
init|(
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|stopWords
argument_list|)
init|)
block|{
name|CharTermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|stopWordsSet
operator|.
name|add
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"IoException parsing stop words list in "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|":"
operator|+
name|ioe
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
name|MoreLikeThisQuery
name|mlt
init|=
operator|new
name|MoreLikeThisQuery
argument_list|(
name|DOMUtils
operator|.
name|getText
argument_list|(
name|e
argument_list|)
argument_list|,
name|fields
argument_list|,
name|analyzer
argument_list|,
name|fields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|setMaxQueryTerms
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"maxQueryTerms"
argument_list|,
name|DEFAULT_MAX_QUERY_TERMS
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFrequency
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"minTermFrequency"
argument_list|,
name|DEFAULT_MIN_TERM_FREQUENCY
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setPercentTermsToMatch
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"percentTermsToMatch"
argument_list|,
name|DEFAULT_PERCENT_TERMS_TO_MATCH
argument_list|)
operator|/
literal|100
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setStopWords
argument_list|(
name|stopWordsSet
argument_list|)
expr_stmt|;
name|int
name|minDocFreq
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"minDocFreq"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|minDocFreq
operator|>=
literal|0
condition|)
block|{
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|minDocFreq
argument_list|)
expr_stmt|;
block|}
name|mlt
operator|.
name|setBoost
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|mlt
return|;
block|}
block|}
end_class
end_unit
