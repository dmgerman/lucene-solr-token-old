begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|solr
operator|.
name|search
operator|.
name|SolrIndexReader
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
name|Map
import|;
end_import
begin_comment
comment|/**  * Obtains the ordinal of the field value from the default Lucene {@link org.apache.lucene.search.FieldCache} using getTermsIndex()  * and reverses the order.  *<br>  * The native lucene index order is used to assign an ordinal value for each field value.  *<br>Field values (terms) are lexicographically ordered by unicode value, and numbered starting at 1.  *<br>  * Example of reverse ordinal (rord):<br>  *  If there were only three field values: "apple","banana","pear"  *<br>then rord("apple")=3, rord("banana")=2, ord("pear")=1  *<p>  *  WARNING: ord() depends on the position in an index and can thus change when other documents are inserted or deleted,  *  or if a MultiSearcher is used.  *<br>  *  WARNING: as of Solr 1.4, ord() and rord() can cause excess memory use since they must use a FieldCache entry  * at the top level reader, while sorting and function queries now use entries at the segment level.  Hence sorting  * or using a different function query, in addition to ord()/rord() will double memory use.  *   * @version $Id$  */
end_comment
begin_class
DECL|class|ReverseOrdFieldSource
specifier|public
class|class
name|ReverseOrdFieldSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|public
name|String
name|field
decl_stmt|;
DECL|method|ReverseOrdFieldSource
specifier|public
name|ReverseOrdFieldSource
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
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"rord("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|IndexReader
name|topReader
init|=
name|reader
decl_stmt|;
if|if
condition|(
name|topReader
operator|instanceof
name|SolrIndexReader
condition|)
block|{
name|SolrIndexReader
name|r
init|=
operator|(
name|SolrIndexReader
operator|)
name|topReader
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|offset
operator|+=
name|r
operator|.
name|getBase
argument_list|()
expr_stmt|;
name|r
operator|=
name|r
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|topReader
operator|=
name|r
expr_stmt|;
block|}
specifier|final
name|int
name|off
init|=
name|offset
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|DocTermsIndex
name|sindex
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|topReader
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|sindex
operator|.
name|numOrd
argument_list|()
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|end
operator|-
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
operator|+
name|off
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|end
operator|-
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
operator|+
name|off
argument_list|)
operator|)
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
call|(
name|long
call|)
argument_list|(
name|end
operator|-
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
operator|+
name|off
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|ordVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|end
operator|-
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
operator|+
name|off
argument_list|)
operator|)
return|;
block|}
specifier|public
name|int
name|numOrd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
call|(
name|double
call|)
argument_list|(
name|end
operator|-
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
operator|+
name|off
argument_list|)
argument_list|)
return|;
block|}
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
operator|(
name|end
operator|-
name|sindex
operator|.
name|getOrd
argument_list|(
name|doc
operator|+
name|off
argument_list|)
operator|)
argument_list|)
return|;
block|}
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
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
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
operator|.
name|getClass
argument_list|()
operator|!=
name|ReverseOrdFieldSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|ReverseOrdFieldSource
name|other
init|=
operator|(
name|ReverseOrdFieldSource
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
name|ReverseOrdFieldSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
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
empty_stmt|;
block|}
end_class
end_unit
