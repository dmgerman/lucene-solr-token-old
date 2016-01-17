begin_unit
begin_package
DECL|package|org.apache.lucene.classification.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|utils
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
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
name|ExecutionException
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
name|ExecutorService
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
name|Executors
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
name|TimeUnit
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
name|TimeoutException
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
name|classification
operator|.
name|ClassificationResult
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
name|classification
operator|.
name|Classifier
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
name|document
operator|.
name|Document
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
name|TermRangeQuery
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
name|util
operator|.
name|BytesRef
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
name|NamedThreadFactory
import|;
end_import
begin_comment
comment|/**  * Utility class to generate the confusion matrix of a {@link Classifier}  */
end_comment
begin_class
DECL|class|ConfusionMatrixGenerator
specifier|public
class|class
name|ConfusionMatrixGenerator
block|{
DECL|method|ConfusionMatrixGenerator
specifier|private
name|ConfusionMatrixGenerator
parameter_list|()
block|{    }
comment|/**    * get the {@link org.apache.lucene.classification.utils.ConfusionMatrixGenerator.ConfusionMatrix} of a given {@link Classifier},    * generated on the given {@link LeafReader}, class and text fields.    *    * @param reader         the {@link LeafReader} containing the index used for creating the {@link Classifier}    * @param classifier     the {@link Classifier} whose confusion matrix has to be generated    * @param classFieldName the name of the Lucene field used as the classifier's output    * @param textFieldName  the nome the Lucene field used as the classifier's input    * @param<T>            the return type of the {@link ClassificationResult} returned by the given {@link Classifier}    * @return a {@link org.apache.lucene.classification.utils.ConfusionMatrixGenerator.ConfusionMatrix}    * @throws IOException if problems occurr while reading the index or using the classifier    */
DECL|method|getConfusionMatrix
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ConfusionMatrix
name|getConfusionMatrix
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|Classifier
argument_list|<
name|T
argument_list|>
name|classifier
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|String
name|textFieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|NamedThreadFactory
argument_list|(
literal|"confusion-matrix-gen-"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|TermRangeQuery
argument_list|(
name|classFieldName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|double
name|time
init|=
literal|0d
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
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
index|[]
name|correctAnswers
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|classFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|correctAnswers
operator|!=
literal|null
operator|&&
name|correctAnswers
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|correctAnswers
argument_list|)
expr_stmt|;
name|ClassificationResult
argument_list|<
name|T
argument_list|>
name|result
decl_stmt|;
name|String
name|text
init|=
name|doc
operator|.
name|get
argument_list|(
name|textFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// fail if classification takes more than 5s
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|result
operator|=
name|executorService
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
name|classifier
operator|.
name|assignClass
argument_list|(
name|text
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|time
operator|+=
name|end
operator|-
name|start
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|T
name|assignedClass
init|=
name|result
operator|.
name|getAssignedClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|assignedClass
operator|!=
literal|null
condition|)
block|{
name|String
name|classified
init|=
name|assignedClass
operator|instanceof
name|BytesRef
condition|?
operator|(
operator|(
name|BytesRef
operator|)
name|assignedClass
operator|)
operator|.
name|utf8ToString
argument_list|()
else|:
name|assignedClass
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|correctAnswer
decl_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|correctAnswers
argument_list|,
name|classified
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|correctAnswer
operator|=
name|classified
expr_stmt|;
block|}
else|else
block|{
name|correctAnswer
operator|=
name|correctAnswers
index|[
literal|0
index|]
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|stringLongMap
init|=
name|counts
operator|.
name|get
argument_list|(
name|correctAnswer
argument_list|)
decl_stmt|;
if|if
condition|(
name|stringLongMap
operator|!=
literal|null
condition|)
block|{
name|Long
name|aLong
init|=
name|stringLongMap
operator|.
name|get
argument_list|(
name|classified
argument_list|)
decl_stmt|;
if|if
condition|(
name|aLong
operator|!=
literal|null
condition|)
block|{
name|stringLongMap
operator|.
name|put
argument_list|(
name|classified
argument_list|,
name|aLong
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stringLongMap
operator|.
name|put
argument_list|(
name|classified
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|stringLongMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|stringLongMap
operator|.
name|put
argument_list|(
name|classified
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|correctAnswer
argument_list|,
name|stringLongMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|timeoutException
parameter_list|)
block|{
comment|// add timeout
name|time
operator|+=
literal|5000
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
name|executionException
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|executionException
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|ConfusionMatrix
argument_list|(
name|counts
argument_list|,
name|time
operator|/
name|topDocs
operator|.
name|totalHits
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
return|;
block|}
finally|finally
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * a confusion matrix, backed by a {@link Map} representing the linearized matrix    */
DECL|class|ConfusionMatrix
specifier|public
specifier|static
class|class
name|ConfusionMatrix
block|{
DECL|field|linearizedMatrix
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|linearizedMatrix
decl_stmt|;
DECL|field|avgClassificationTime
specifier|private
specifier|final
name|double
name|avgClassificationTime
decl_stmt|;
DECL|field|numberOfEvaluatedDocs
specifier|private
specifier|final
name|int
name|numberOfEvaluatedDocs
decl_stmt|;
DECL|field|accuracy
specifier|private
name|double
name|accuracy
init|=
operator|-
literal|1d
decl_stmt|;
DECL|method|ConfusionMatrix
specifier|private
name|ConfusionMatrix
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|linearizedMatrix
parameter_list|,
name|double
name|avgClassificationTime
parameter_list|,
name|int
name|numberOfEvaluatedDocs
parameter_list|)
block|{
name|this
operator|.
name|linearizedMatrix
operator|=
name|linearizedMatrix
expr_stmt|;
name|this
operator|.
name|avgClassificationTime
operator|=
name|avgClassificationTime
expr_stmt|;
name|this
operator|.
name|numberOfEvaluatedDocs
operator|=
name|numberOfEvaluatedDocs
expr_stmt|;
block|}
comment|/**      * get the linearized confusion matrix as a {@link Map}      *      * @return a {@link Map} whose keys are the correct classification answers and whose values are the actual answers'      * counts      */
DECL|method|getLinearizedMatrix
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|getLinearizedMatrix
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|linearizedMatrix
argument_list|)
return|;
block|}
comment|/**      * calculate precision on the given class      *      * @param klass the class to calculate the precision for      * @return the precision for the given class      */
DECL|method|getPrecision
specifier|public
name|double
name|getPrecision
parameter_list|(
name|String
name|klass
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|classifications
init|=
name|linearizedMatrix
operator|.
name|get
argument_list|(
name|klass
argument_list|)
decl_stmt|;
name|double
name|tp
init|=
literal|0
decl_stmt|;
name|double
name|fp
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|classifications
operator|!=
literal|null
condition|)
block|{
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
name|classifications
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|klass
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|tp
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|values
range|:
name|linearizedMatrix
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|values
operator|.
name|containsKey
argument_list|(
name|klass
argument_list|)
condition|)
block|{
name|fp
operator|+=
name|values
operator|.
name|get
argument_list|(
name|klass
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|tp
operator|+
name|fp
operator|>
literal|0
condition|?
name|tp
operator|/
operator|(
name|tp
operator|+
name|fp
operator|)
else|:
literal|0
return|;
block|}
comment|/**      * calculate recall on the given class      *      * @param klass the class to calculate the recall for      * @return the recall for the given class      */
DECL|method|getRecall
specifier|public
name|double
name|getRecall
parameter_list|(
name|String
name|klass
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|classifications
init|=
name|linearizedMatrix
operator|.
name|get
argument_list|(
name|klass
argument_list|)
decl_stmt|;
name|double
name|tp
init|=
literal|0
decl_stmt|;
name|double
name|fn
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|classifications
operator|!=
literal|null
condition|)
block|{
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
name|classifications
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|klass
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|tp
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fn
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|tp
operator|+
name|fn
operator|>
literal|0
condition|?
name|tp
operator|/
operator|(
name|tp
operator|+
name|fn
operator|)
else|:
literal|0
return|;
block|}
comment|/**      * get the F-1 measure of the given class      *      * @param klass the class to calculate the F-1 measure for      * @return the F-1 measure for the given class      */
DECL|method|getF1Measure
specifier|public
name|double
name|getF1Measure
parameter_list|(
name|String
name|klass
parameter_list|)
block|{
name|double
name|recall
init|=
name|getRecall
argument_list|(
name|klass
argument_list|)
decl_stmt|;
name|double
name|precision
init|=
name|getPrecision
argument_list|(
name|klass
argument_list|)
decl_stmt|;
return|return
name|precision
operator|>
literal|0
operator|&&
name|recall
operator|>
literal|0
condition|?
literal|2
operator|*
name|precision
operator|*
name|recall
operator|/
operator|(
name|precision
operator|+
name|recall
operator|)
else|:
literal|0
return|;
block|}
comment|/**      * Calculate accuracy on this confusion matrix using the formula:      * {@literal accuracy = correctly-classified / (correctly-classified + wrongly-classified)}      *      * @return the accuracy      */
DECL|method|getAccuracy
specifier|public
name|double
name|getAccuracy
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|accuracy
operator|==
operator|-
literal|1
condition|)
block|{
name|double
name|cc
init|=
literal|0d
decl_stmt|;
name|double
name|wc
init|=
literal|0d
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|linearizedMatrix
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|correctAnswer
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
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
name|classifiedAnswers
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|value
init|=
name|classifiedAnswers
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|correctAnswer
operator|.
name|equals
argument_list|(
name|classifiedAnswers
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|cc
operator|+=
name|value
expr_stmt|;
block|}
else|else
block|{
name|wc
operator|+=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
name|this
operator|.
name|accuracy
operator|=
name|cc
operator|/
operator|(
name|cc
operator|+
name|wc
operator|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|accuracy
return|;
block|}
comment|/**      * get the precision (see {@link #getPrecision(String)}) over all the classes.      *      * @return the precision as computed from the whole confusion matrix      */
DECL|method|getPrecision
specifier|public
name|double
name|getPrecision
parameter_list|()
block|{
name|double
name|tp
init|=
literal|0
decl_stmt|;
name|double
name|fp
init|=
operator|-
name|linearizedMatrix
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|classification
range|:
name|linearizedMatrix
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|klass
init|=
name|classification
operator|.
name|getKey
argument_list|()
decl_stmt|;
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
name|classification
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|klass
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|tp
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|values
range|:
name|linearizedMatrix
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|values
operator|.
name|containsKey
argument_list|(
name|klass
argument_list|)
condition|)
block|{
name|fp
operator|+=
name|values
operator|.
name|get
argument_list|(
name|klass
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|tp
operator|+
name|fp
operator|>
literal|0
condition|?
name|tp
operator|/
operator|(
name|tp
operator|+
name|fp
operator|)
else|:
literal|0
return|;
block|}
comment|/**      * get the recall (see {@link #getRecall(String)}) over all the classes      *      * @return the recall as computed from the whole confusion matrix      */
DECL|method|getRecall
specifier|public
name|double
name|getRecall
parameter_list|()
block|{
name|double
name|tp
init|=
literal|0
decl_stmt|;
name|double
name|fn
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|classification
range|:
name|linearizedMatrix
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|klass
init|=
name|classification
operator|.
name|getKey
argument_list|()
decl_stmt|;
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
name|classification
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|klass
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|tp
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fn
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|tp
operator|+
name|fn
operator|>
literal|0
condition|?
name|tp
operator|/
operator|(
name|tp
operator|+
name|fn
operator|)
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ConfusionMatrix{"
operator|+
literal|"linearizedMatrix="
operator|+
name|linearizedMatrix
operator|+
literal|", avgClassificationTime="
operator|+
name|avgClassificationTime
operator|+
literal|", numberOfEvaluatedDocs="
operator|+
name|numberOfEvaluatedDocs
operator|+
literal|'}'
return|;
block|}
comment|/**      * get the average classification time in milliseconds      *      * @return the avg classification time      */
DECL|method|getAvgClassificationTime
specifier|public
name|double
name|getAvgClassificationTime
parameter_list|()
block|{
return|return
name|avgClassificationTime
return|;
block|}
comment|/**      * get the no. of documents evaluated while generating this confusion matrix      *      * @return the no. of documents evaluated      */
DECL|method|getNumberOfEvaluatedDocs
specifier|public
name|int
name|getNumberOfEvaluatedDocs
parameter_list|()
block|{
return|return
name|numberOfEvaluatedDocs
return|;
block|}
block|}
block|}
end_class
end_unit
