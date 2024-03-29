begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * Extra lucene sorting utilities&amp; convenience methods  *  *  *  */
end_comment
begin_class
DECL|class|Sorting
specifier|public
class|class
name|Sorting
block|{
comment|/** Returns a {@link SortField} for a string field.    *  If nullLast and nullFirst are both false, then default lucene string sorting is used where    *  null strings sort first in an ascending sort, and last in a descending sort.    *    * @param fieldName   the name of the field to sort on    * @param reverse     true for a reverse (desc) sort    * @param nullLast    true if null should come last, regardless of sort order    * @param nullFirst   true if null should come first, regardless of sort order    * @return SortField    */
DECL|method|getStringSortField
specifier|public
specifier|static
name|SortField
name|getStringSortField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|boolean
name|nullLast
parameter_list|,
name|boolean
name|nullFirst
parameter_list|)
block|{
name|SortField
name|sortField
init|=
operator|new
name|SortField
argument_list|(
name|fieldName
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
name|reverse
argument_list|)
decl_stmt|;
name|applyMissingFirstLast
argument_list|(
name|sortField
argument_list|,
name|reverse
argument_list|,
name|nullLast
argument_list|,
name|nullFirst
argument_list|)
expr_stmt|;
return|return
name|sortField
return|;
block|}
comment|/** Like {@link #getStringSortField}) except safe for tokenized fields */
DECL|method|getTextSortField
specifier|public
specifier|static
name|SortField
name|getTextSortField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|boolean
name|nullLast
parameter_list|,
name|boolean
name|nullFirst
parameter_list|)
block|{
name|SortField
name|sortField
init|=
operator|new
name|SortedSetSortField
argument_list|(
name|fieldName
argument_list|,
name|reverse
argument_list|)
decl_stmt|;
name|applyMissingFirstLast
argument_list|(
name|sortField
argument_list|,
name|reverse
argument_list|,
name|nullLast
argument_list|,
name|nullFirst
argument_list|)
expr_stmt|;
return|return
name|sortField
return|;
block|}
DECL|method|applyMissingFirstLast
specifier|private
specifier|static
name|void
name|applyMissingFirstLast
parameter_list|(
name|SortField
name|in
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|boolean
name|nullLast
parameter_list|,
name|boolean
name|nullFirst
parameter_list|)
block|{
if|if
condition|(
name|nullFirst
operator|&&
name|nullLast
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot specify missing values as both first and last"
argument_list|)
throw|;
block|}
comment|// 4 cases:
comment|// missingFirst / forward: default lucene behavior
comment|// missingFirst / reverse: set sortMissingLast
comment|// missingLast  / forward: set sortMissingLast
comment|// missingLast  / reverse: default lucene behavior
if|if
condition|(
name|nullFirst
operator|&&
name|reverse
condition|)
block|{
name|in
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_LAST
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nullLast
operator|&&
operator|!
name|reverse
condition|)
block|{
name|in
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_LAST
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
