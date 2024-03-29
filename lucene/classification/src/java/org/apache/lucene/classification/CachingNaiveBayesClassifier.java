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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|MultiFields
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
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|TermQuery
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
name|TotalHitCountCollector
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
comment|/**  * A simplistic Lucene based NaiveBayes classifier, with caching feature, see  *<code>http://en.wikipedia.org/wiki/Naive_Bayes_classifier</code>  *<p>  * This is NOT an online classifier.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CachingNaiveBayesClassifier
specifier|public
class|class
name|CachingNaiveBayesClassifier
extends|extends
name|SimpleNaiveBayesClassifier
block|{
comment|//for caching classes this will be the classification class list
DECL|field|cclasses
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
name|cclasses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// it's a term-inmap style map, where the inmap contains class-hit pairs to the
comment|// upper term
DECL|field|termCClassHitCache
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|termCClassHitCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// the term frequency in classes
DECL|field|classTermFreq
specifier|private
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Double
argument_list|>
name|classTermFreq
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|justCachedTerms
specifier|private
name|boolean
name|justCachedTerms
decl_stmt|;
DECL|field|docsWithClassSize
specifier|private
name|int
name|docsWithClassSize
decl_stmt|;
comment|/**    * Creates a new NaiveBayes classifier with inside caching. If you want less memory usage you could call    * {@link #reInitCache(int, boolean) reInitCache()}.    *    * @param leafReader     the reader on the index to be used for classification    * @param analyzer       an {@link Analyzer} used to analyze unseen text    * @param query          a {@link Query} to eventually filter the docs used for training the classifier, or {@code null}    *                       if all the indexed docs should be used    * @param classFieldName the name of the field used as the output for the classifier    * @param textFieldNames the name of the fields used as the inputs for the classifier    */
DECL|method|CachingNaiveBayesClassifier
specifier|public
name|CachingNaiveBayesClassifier
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
name|String
name|classFieldName
parameter_list|,
name|String
modifier|...
name|textFieldNames
parameter_list|)
block|{
name|super
argument_list|(
name|leafReader
argument_list|,
name|analyzer
argument_list|,
name|query
argument_list|,
name|classFieldName
argument_list|,
name|textFieldNames
argument_list|)
expr_stmt|;
comment|// building the cache
try|try
block|{
name|reInitCache
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|assignClassNormalizedList
specifier|protected
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignClassNormalizedList
parameter_list|(
name|String
name|inputDocument
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|tokenizedText
init|=
name|tokenize
argument_list|(
name|inputDocument
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
name|calculateLogLikelihood
argument_list|(
name|tokenizedText
argument_list|)
decl_stmt|;
comment|// normalization
comment|// The values transforms to a 0-1 range
name|ArrayList
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|asignedClassesNorm
init|=
name|super
operator|.
name|normClassificationResults
argument_list|(
name|assignedClasses
argument_list|)
decl_stmt|;
return|return
name|asignedClassesNorm
return|;
block|}
DECL|method|calculateLogLikelihood
specifier|private
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|calculateLogLikelihood
parameter_list|(
name|String
index|[]
name|tokenizedText
parameter_list|)
throws|throws
name|IOException
block|{
comment|// initialize the return List
name|ArrayList
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|cclass
range|:
name|cclasses
control|)
block|{
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|cr
init|=
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|cclass
argument_list|,
literal|0d
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|cr
argument_list|)
expr_stmt|;
block|}
comment|// for each word
for|for
control|(
name|String
name|word
range|:
name|tokenizedText
control|)
block|{
comment|// search with text:word for all class:c
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|hitsInClasses
init|=
name|getWordFreqForClassess
argument_list|(
name|word
argument_list|)
decl_stmt|;
comment|// for each class
for|for
control|(
name|BytesRef
name|cclass
range|:
name|cclasses
control|)
block|{
name|Integer
name|hitsI
init|=
name|hitsInClasses
operator|.
name|get
argument_list|(
name|cclass
argument_list|)
decl_stmt|;
comment|// if the word is out of scope hitsI could be null
name|int
name|hits
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|hitsI
operator|!=
literal|null
condition|)
block|{
name|hits
operator|=
name|hitsI
expr_stmt|;
block|}
comment|// num : count the no of times the word appears in documents of class c(+1)
name|double
name|num
init|=
name|hits
operator|+
literal|1
decl_stmt|;
comment|// +1 is added because of add 1 smoothing
comment|// den : for the whole dictionary, count the no of times a word appears in documents of class c (+|V|)
name|double
name|den
init|=
name|classTermFreq
operator|.
name|get
argument_list|(
name|cclass
argument_list|)
operator|+
name|docsWithClassSize
decl_stmt|;
comment|// P(w|c) = num/den
name|double
name|wordProbability
init|=
name|num
operator|/
name|den
decl_stmt|;
comment|// modify the value in the result list item
name|int
name|removeIdx
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|cr
range|:
name|ret
control|)
block|{
if|if
condition|(
name|cr
operator|.
name|getAssignedClass
argument_list|()
operator|.
name|equals
argument_list|(
name|cclass
argument_list|)
condition|)
block|{
name|removeIdx
operator|=
name|i
expr_stmt|;
break|break;
block|}
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|removeIdx
operator|>=
literal|0
condition|)
block|{
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|toRemove
init|=
name|ret
operator|.
name|get
argument_list|(
name|removeIdx
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|toRemove
operator|.
name|getAssignedClass
argument_list|()
argument_list|,
name|toRemove
operator|.
name|getScore
argument_list|()
operator|+
name|Math
operator|.
name|log
argument_list|(
name|wordProbability
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|.
name|remove
argument_list|(
name|removeIdx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// log(P(d|c)) = log(P(w1|c))+...+log(P(wn|c))
return|return
name|ret
return|;
block|}
DECL|method|getWordFreqForClassess
specifier|private
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|getWordFreqForClassess
parameter_list|(
name|String
name|word
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
name|insertPoint
decl_stmt|;
name|insertPoint
operator|=
name|termCClassHitCache
operator|.
name|get
argument_list|(
name|word
argument_list|)
expr_stmt|;
comment|// if we get the answer from the cache
if|if
condition|(
name|insertPoint
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|insertPoint
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|insertPoint
return|;
block|}
block|}
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|searched
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// if we dont get the answer, but it's relevant we must search it and insert to the cache
if|if
condition|(
name|insertPoint
operator|!=
literal|null
operator|||
operator|!
name|justCachedTerms
condition|)
block|{
for|for
control|(
name|BytesRef
name|cclass
range|:
name|cclasses
control|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|booleanQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|subQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
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
name|subQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|textFieldName
argument_list|,
name|word
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
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|subQuery
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
name|cclass
argument_list|)
argument_list|)
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
name|booleanQuery
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
name|TotalHitCountCollector
name|totalHitCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|booleanQuery
operator|.
name|build
argument_list|()
argument_list|,
name|totalHitCountCollector
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|totalHitCountCollector
operator|.
name|getTotalHits
argument_list|()
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|0
condition|)
block|{
name|searched
operator|.
name|put
argument_list|(
name|cclass
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|insertPoint
operator|!=
literal|null
condition|)
block|{
comment|// threadsafe and concurrent write
name|termCClassHitCache
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|searched
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|searched
return|;
block|}
comment|/**    * This function is building the frame of the cache. The cache is storing the    * word occurrences to the memory after those searched once. This cache can    * made 2-100x speedup in proper use, but can eat lot of memory. There is an    * option to lower the memory consume, if a word have really low occurrence in    * the index you could filter it out. The other parameter is switching between    * the term searching, if it true, just the terms in the skeleton will be    * searched, but if it false the terms whoes not in the cache will be searched    * out too (but not cached).    *    * @param minTermOccurrenceInCache Lower cache size with higher value.    * @param justCachedTerms          The switch for fully exclude low occurrence docs.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|reInitCache
specifier|public
name|void
name|reInitCache
parameter_list|(
name|int
name|minTermOccurrenceInCache
parameter_list|,
name|boolean
name|justCachedTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|justCachedTerms
operator|=
name|justCachedTerms
expr_stmt|;
name|this
operator|.
name|docsWithClassSize
operator|=
name|countDocsWithClass
argument_list|()
expr_stmt|;
name|termCClassHitCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cclasses
operator|.
name|clear
argument_list|()
expr_stmt|;
name|classTermFreq
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// build the cache for the word
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|frequencyMap
init|=
operator|new
name|HashMap
argument_list|<>
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
name|TermsEnum
name|termsEnum
init|=
name|leafReader
operator|.
name|terms
argument_list|(
name|textFieldName
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|term
argument_list|()
decl_stmt|;
name|String
name|termText
init|=
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|long
name|frequency
init|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
decl_stmt|;
name|Long
name|lastfreq
init|=
name|frequencyMap
operator|.
name|get
argument_list|(
name|termText
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastfreq
operator|!=
literal|null
condition|)
name|frequency
operator|+=
name|lastfreq
expr_stmt|;
name|frequencyMap
operator|.
name|put
argument_list|(
name|termText
argument_list|,
name|frequency
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|frequencyMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|>
name|minTermOccurrenceInCache
condition|)
block|{
name|termCClassHitCache
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ConcurrentHashMap
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// fill the class list
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|leafReader
argument_list|,
name|classFieldName
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|cclasses
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// fill the classTermFreq map
for|for
control|(
name|BytesRef
name|cclass
range|:
name|cclasses
control|)
block|{
name|double
name|avgNumberOfUniqueTerms
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|textFieldName
range|:
name|textFieldNames
control|)
block|{
name|terms
operator|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|leafReader
argument_list|,
name|textFieldName
argument_list|)
expr_stmt|;
name|long
name|numPostings
init|=
name|terms
operator|.
name|getSumDocFreq
argument_list|()
decl_stmt|;
comment|// number of term/doc pairs
name|avgNumberOfUniqueTerms
operator|+=
name|numPostings
operator|/
operator|(
name|double
operator|)
name|terms
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
name|int
name|docsWithC
init|=
name|leafReader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
name|cclass
argument_list|)
argument_list|)
decl_stmt|;
name|classTermFreq
operator|.
name|put
argument_list|(
name|cclass
argument_list|,
name|avgNumberOfUniqueTerms
operator|*
name|docsWithC
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
