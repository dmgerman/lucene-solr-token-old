begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
package|;
end_package
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
operator|.
name|AtomicReaderContext
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
name|FieldCache
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
name|FieldCache
operator|.
name|DocTermsIndex
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
begin_comment
comment|/**  * Expert: obtains the ordinal of the field value from the default Lucene   * {@link org.apache.lucene.search.FieldCache Fieldcache} using getStringIndex().  *<p>  * The native lucene index order is used to assign an ordinal value for each field value.  *<p  * Field values (terms) are lexicographically ordered by unicode value, and numbered starting at 1.  *<p>  * Example:  *<br>If there were only three field values: "apple","banana","pear"  *<br>then ord("apple")=1, ord("banana")=2, ord("pear")=3  *<p>  * WARNING:   * ord() depends on the position in an index and can thus change   * when other documents are inserted or deleted,  * or if a MultiSearcher is used.   *  * @lucene.experimental  *  *<p><b>NOTE</b>: with the switch in 2.9 to segment-based  * searching, if {@link #getValues} is invoked with a  * composite (multi-segment) reader, this can easily cause  * double RAM usage for the values in the FieldCache.  It's  * best to switch your application to pass only atomic  * (single segment) readers to this API.</p>  */
end_comment
begin_class
DECL|class|OrdFieldSource
specifier|public
class|class
name|OrdFieldSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
comment|/**     * Constructor for a certain field.    * @param field field whose values order is used.      */
DECL|method|OrdFieldSource
specifier|public
name|OrdFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.ValueSource#description() */
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"ord("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.ValueSource#getValues(org.apache.lucene.index.IndexReader) */
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocTermsIndex
name|termsIndex
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|context
operator|.
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#floatVal(int) */
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#strVal(int) */
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// the string value of the ordinal, not the string itself
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#toString(int) */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#getInnerArray() */
annotation|@
name|Override
name|Object
name|getInnerArray
parameter_list|()
block|{
return|return
name|termsIndex
return|;
block|}
block|}
return|;
block|}
comment|/*(non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|OrdFieldSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|OrdFieldSource
name|other
init|=
operator|(
name|OrdFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
DECL|field|hcode
specifier|private
specifier|static
specifier|final
name|int
name|hcode
init|=
name|OrdFieldSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
comment|/*(non-Javadoc) @see java.lang.Object#hashCode() */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hcode
operator|+
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
