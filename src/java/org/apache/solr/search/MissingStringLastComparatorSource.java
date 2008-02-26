begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|search
operator|.
name|*
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
name|IndexReader
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
comment|/**  * A {@link SortComparatorSource} for strings that orders null values after non-null values.  * Based on FieldSortedHitQueue.comparatorString  *<p>  *  * @version $Id$  *  */
end_comment
begin_comment
comment|// move to apache package and make public if it is accepted as a patch
end_comment
begin_class
DECL|class|MissingStringLastComparatorSource
specifier|public
class|class
name|MissingStringLastComparatorSource
implements|implements
name|SortComparatorSource
block|{
DECL|field|bigString
specifier|public
specifier|static
specifier|final
name|String
name|bigString
init|=
literal|"\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffffNULL_VAL"
decl_stmt|;
DECL|field|missingValueProxy
specifier|private
specifier|final
name|String
name|missingValueProxy
decl_stmt|;
DECL|method|MissingStringLastComparatorSource
specifier|public
name|MissingStringLastComparatorSource
parameter_list|()
block|{
name|this
argument_list|(
name|bigString
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Returns the value used to sort the given document.  The 	 * object returned must implement the java.io.Serializable 	 * interface.  This is used by multisearchers to determine how to collate results from their searchers. 	 * @see FieldDoc 	 * @param i Document 	 * @return Serializable object 	 */
comment|/** Creates a {@link SortComparatorSource} that uses<tt>missingValueProxy</tt> as the value to return from ScoreDocComparator.sortValue()    * which is only used my multisearchers to determine how to collate results from their searchers.    *    * @param missingValueProxy   The value returned when sortValue() is called for a document missing the sort field.    * This value is *not* normally used for sorting, but used to create    */
DECL|method|MissingStringLastComparatorSource
specifier|public
name|MissingStringLastComparatorSource
parameter_list|(
name|String
name|missingValueProxy
parameter_list|)
block|{
name|this
operator|.
name|missingValueProxy
operator|=
name|missingValueProxy
expr_stmt|;
block|}
DECL|method|newComparator
specifier|public
name|ScoreDocComparator
name|newComparator
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|StringIndex
name|index
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStringIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
comment|// :HACK:
comment|// final String lastString =
comment|// (index.lookup[index.lookup.length-1]+"X").intern();
comment|//
comment|// Note: basing lastStringValue on the StringIndex won't work
comment|// with a multisearcher.
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|int
name|fi
init|=
name|index
operator|.
name|order
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|fj
init|=
name|index
operator|.
name|order
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
comment|// 0 is the magic position of null
comment|/**** alternate logic          if (fi< fj&& fi != 0) return -1;          if (fj< fi&& fj != 0) return 1;          if (fi==fj) return 0;          return fi==0 ? 1 : -1;          ****/
if|if
condition|(
name|fi
operator|==
name|fj
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|fi
operator|==
literal|0
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|fj
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
return|return
name|fi
operator|<
name|fj
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
name|int
name|f
init|=
name|index
operator|.
name|order
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
return|return
operator|(
literal|0
operator|==
name|f
operator|)
condition|?
name|missingValueProxy
else|:
name|index
operator|.
name|lookup
index|[
name|f
index|]
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|CUSTOM
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
