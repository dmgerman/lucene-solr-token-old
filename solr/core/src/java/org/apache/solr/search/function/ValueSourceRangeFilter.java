begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|BitsFilteredDocIdSet
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
name|util
operator|.
name|Bits
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
name|SolrFilter
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
comment|/**  * RangeFilter over a ValueSource.  */
end_comment
begin_class
DECL|class|ValueSourceRangeFilter
specifier|public
class|class
name|ValueSourceRangeFilter
extends|extends
name|SolrFilter
block|{
DECL|field|valueSource
specifier|private
specifier|final
name|ValueSource
name|valueSource
decl_stmt|;
DECL|field|lowerVal
specifier|private
specifier|final
name|String
name|lowerVal
decl_stmt|;
DECL|field|upperVal
specifier|private
specifier|final
name|String
name|upperVal
decl_stmt|;
DECL|field|includeLower
specifier|private
specifier|final
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|private
specifier|final
name|boolean
name|includeUpper
decl_stmt|;
DECL|method|ValueSourceRangeFilter
specifier|public
name|ValueSourceRangeFilter
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|valueSource
operator|=
name|valueSource
expr_stmt|;
name|this
operator|.
name|lowerVal
operator|=
name|lowerVal
expr_stmt|;
name|this
operator|.
name|upperVal
operator|=
name|upperVal
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|lowerVal
operator|!=
literal|null
operator|&&
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|upperVal
operator|!=
literal|null
operator|&&
name|includeUpper
expr_stmt|;
block|}
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|()
block|{
return|return
name|valueSource
return|;
block|}
DECL|method|getLowerVal
specifier|public
name|String
name|getLowerVal
parameter_list|()
block|{
return|return
name|lowerVal
return|;
block|}
DECL|method|getUpperVal
specifier|public
name|String
name|getUpperVal
parameter_list|()
block|{
return|return
name|upperVal
return|;
block|}
DECL|method|isIncludeLower
specifier|public
name|boolean
name|isIncludeLower
parameter_list|()
block|{
return|return
name|includeLower
return|;
block|}
DECL|method|isIncludeUpper
specifier|public
name|boolean
name|isIncludeUpper
parameter_list|()
block|{
return|return
name|includeUpper
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
specifier|final
name|Map
name|context
parameter_list|,
specifier|final
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|valueSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
operator|.
name|getRangeScorer
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|bits
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// don't use random access
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
block|}
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|valueSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"frange("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|valueSource
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"):"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|includeLower
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|lowerVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|lowerVal
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|upperVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|upperVal
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|includeUpper
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|ValueSourceRangeFilter
operator|)
condition|)
return|return
literal|false
return|;
name|ValueSourceRangeFilter
name|other
init|=
operator|(
name|ValueSourceRangeFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|valueSource
operator|.
name|equals
argument_list|(
name|other
operator|.
name|valueSource
argument_list|)
operator|||
name|this
operator|.
name|includeLower
operator|!=
name|other
operator|.
name|includeLower
operator|||
name|this
operator|.
name|includeUpper
operator|!=
name|other
operator|.
name|includeUpper
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|lowerVal
operator|!=
literal|null
condition|?
operator|!
name|this
operator|.
name|lowerVal
operator|.
name|equals
argument_list|(
name|other
operator|.
name|lowerVal
argument_list|)
else|:
name|other
operator|.
name|lowerVal
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|upperVal
operator|!=
literal|null
condition|?
operator|!
name|this
operator|.
name|upperVal
operator|.
name|equals
argument_list|(
name|other
operator|.
name|upperVal
argument_list|)
else|:
name|other
operator|.
name|upperVal
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|valueSource
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|+=
name|lowerVal
operator|!=
literal|null
condition|?
name|lowerVal
operator|.
name|hashCode
argument_list|()
else|:
literal|0x572353db
expr_stmt|;
name|h
operator|=
operator|(
name|h
operator|<<
literal|16
operator|)
operator||
operator|(
name|h
operator|>>>
literal|16
operator|)
expr_stmt|;
comment|// rotate to distinguish lower from upper
name|h
operator|+=
operator|(
name|upperVal
operator|!=
literal|null
condition|?
operator|(
name|upperVal
operator|.
name|hashCode
argument_list|()
operator|)
else|:
literal|0xe16fe9e7
operator|)
expr_stmt|;
name|h
operator|+=
operator|(
name|includeLower
condition|?
literal|0xdaa47978
else|:
literal|0
operator|)
operator|+
operator|(
name|includeUpper
condition|?
literal|0x9e634b57
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
