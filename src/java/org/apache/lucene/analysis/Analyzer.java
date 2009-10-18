begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|CloseableThreadLocal
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
name|store
operator|.
name|AlreadyClosedException
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
name|Fieldable
import|;
end_import
begin_comment
comment|/** An Analyzer builds TokenStreams, which analyze text.  It thus represents a  *  policy for extracting index terms from text.  *<p>  *  Typical implementations first build a Tokenizer, which breaks the stream of  *  characters from the Reader into raw Tokens.  One or more TokenFilters may  *  then be applied to the output of the Tokenizer.  */
end_comment
begin_class
DECL|class|Analyzer
specifier|public
specifier|abstract
class|class
name|Analyzer
implements|implements
name|Closeable
block|{
comment|/** Creates a TokenStream which tokenizes all the text in the provided    * Reader.  Must be able to handle null field name for    * backward compatibility.    */
DECL|method|tokenStream
specifier|public
specifier|abstract
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
function_decl|;
comment|/** Creates a TokenStream that is allowed to be re-used    *  from the previous time that the same thread called    *  this method.  Callers that do not need to use more    *  than one TokenStream at the same time from this    *  analyzer should use this method for better    *  performance.    */
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|field|tokenStreams
specifier|private
name|CloseableThreadLocal
name|tokenStreams
init|=
operator|new
name|CloseableThreadLocal
argument_list|()
decl_stmt|;
comment|/** Used by Analyzers that implement reusableTokenStream    *  to retrieve previously saved TokenStreams for re-use    *  by the same thread. */
DECL|method|getPreviousTokenStream
specifier|protected
name|Object
name|getPreviousTokenStream
parameter_list|()
block|{
try|try
block|{
return|return
name|tokenStreams
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
if|if
condition|(
name|tokenStreams
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Analyzer is closed"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|npe
throw|;
block|}
block|}
block|}
comment|/** Used by Analyzers that implement reusableTokenStream    *  to save a TokenStream for later re-use by the same    *  thread. */
DECL|method|setPreviousTokenStream
specifier|protected
name|void
name|setPreviousTokenStream
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
try|try
block|{
name|tokenStreams
operator|.
name|set
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
if|if
condition|(
name|tokenStreams
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this Analyzer is closed"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|npe
throw|;
block|}
block|}
block|}
DECL|field|overridesTokenStreamMethod
specifier|protected
name|boolean
name|overridesTokenStreamMethod
decl_stmt|;
comment|/** @deprecated This is only present to preserve    *  back-compat of classes that subclass a core analyzer    *  and override tokenStream but not reusableTokenStream */
DECL|method|setOverridesTokenStreamMethod
specifier|protected
name|void
name|setOverridesTokenStreamMethod
parameter_list|(
name|Class
name|baseClass
parameter_list|)
block|{
specifier|final
name|Class
index|[]
name|params
init|=
operator|new
name|Class
index|[
literal|2
index|]
decl_stmt|;
name|params
index|[
literal|0
index|]
operator|=
name|String
operator|.
name|class
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
name|Reader
operator|.
name|class
expr_stmt|;
try|try
block|{
name|Method
name|m
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"tokenStream"
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|overridesTokenStreamMethod
operator|=
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|baseClass
expr_stmt|;
block|}
else|else
block|{
name|overridesTokenStreamMethod
operator|=
literal|false
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
name|overridesTokenStreamMethod
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**    * Invoked before indexing a Fieldable instance if    * terms have already been added to that field.  This allows custom    * analyzers to place an automatic position increment gap between    * Fieldable instances using the same field name.  The default value    * position increment gap is 0.  With a 0 position increment gap and    * the typical default token position increment of 1, all terms in a field,    * including across Fieldable instances, are in successive positions, allowing    * exact PhraseQuery matches, for instance, across Fieldable instance boundaries.    *    * @param fieldName Fieldable name being indexed.    * @return position increment gap, added to the next token emitted from {@link #tokenStream(String,Reader)}    */
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Just like {@link #getPositionIncrementGap}, except for    * Token offsets instead.  By default this returns 1 for    * tokenized fields and, as if the fields were joined    * with an extra space character, and 0 for un-tokenized    * fields.  This method is only called if the field    * produced at least one token for indexing.    *    * @param field the field just indexed    * @return offset gap, added to the next token emitted from {@link #tokenStream(String,Reader)}    */
DECL|method|getOffsetGap
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
return|return
literal|1
return|;
else|else
return|return
literal|0
return|;
block|}
comment|/** Frees persistent resources used by this Analyzer */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|tokenStreams
operator|.
name|close
argument_list|()
expr_stmt|;
name|tokenStreams
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
