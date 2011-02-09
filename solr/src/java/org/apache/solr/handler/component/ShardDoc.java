begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|SortField
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
name|FieldComparatorSource
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
name|PriorityQueue
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|MissingStringLastComparatorSource
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|ArrayList
import|;
end_import
begin_class
DECL|class|ShardDoc
specifier|public
class|class
name|ShardDoc
block|{
DECL|field|shard
specifier|public
name|String
name|shard
decl_stmt|;
DECL|field|shardAddress
specifier|public
name|String
name|shardAddress
decl_stmt|;
comment|// TODO
DECL|field|orderInShard
name|int
name|orderInShard
decl_stmt|;
comment|// the position of this doc within the shard... this can be used
comment|// to short-circuit comparisons if the shard is equal, and can
comment|// also be used to break ties within the same shard.
DECL|field|id
name|Object
name|id
decl_stmt|;
comment|// this is currently the uniqueKeyField but
comment|// may be replaced with internal docid in a future release.
DECL|field|score
name|Float
name|score
decl_stmt|;
DECL|field|sortFieldValues
name|NamedList
name|sortFieldValues
decl_stmt|;
comment|// sort field values for *all* docs in a particular shard.
comment|// this doc's values are in position orderInShard
comment|// TODO: store the SolrDocument here?
comment|// Store the order in the merged list for lookup when getting stored fields?
comment|// (other components need this ordering to store data in order, like highlighting)
comment|// but we shouldn't expose uniqueKey (have a map by it) until the stored-field
comment|// retrieval stage.
DECL|field|positionInResponse
name|int
name|positionInResponse
decl_stmt|;
comment|// the ordinal position in the merged response arraylist
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"id="
operator|+
name|id
operator|+
literal|" ,score="
operator|+
name|score
operator|+
literal|" ,shard="
operator|+
name|shard
operator|+
literal|" ,orderInShard="
operator|+
name|orderInShard
operator|+
literal|" ,positionInResponse="
operator|+
name|positionInResponse
operator|+
literal|" ,sortFieldValues="
operator|+
name|sortFieldValues
return|;
block|}
block|}
end_class
begin_comment
comment|// used by distributed search to merge results.
end_comment
begin_class
DECL|class|ShardFieldSortedHitQueue
class|class
name|ShardFieldSortedHitQueue
extends|extends
name|PriorityQueue
block|{
comment|/** Stores a comparator corresponding to each field being sorted by */
DECL|field|comparators
specifier|protected
name|Comparator
index|[]
name|comparators
decl_stmt|;
comment|/** Stores the sort criteria being used. */
DECL|field|fields
specifier|protected
name|SortField
index|[]
name|fields
decl_stmt|;
comment|/** The order of these fieldNames should correspond to the order of sort field values retrieved from the shard */
DECL|field|fieldNames
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ShardFieldSortedHitQueue
specifier|public
name|ShardFieldSortedHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|fields
operator|.
name|length
decl_stmt|;
name|comparators
operator|=
operator|new
name|Comparator
index|[
name|n
index|]
expr_stmt|;
name|this
operator|.
name|fields
operator|=
operator|new
name|SortField
index|[
name|n
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
operator|++
name|i
control|)
block|{
comment|// keep track of the named fields
name|int
name|type
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|SortField
operator|.
name|SCORE
operator|&&
name|type
operator|!=
name|SortField
operator|.
name|DOC
condition|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldname
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
decl_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|getCachedComparator
argument_list|(
name|fieldname
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getLocale
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getComparatorSource
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|STRING
condition|)
block|{
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getLocale
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("%%%%%%%%%%%%%%%%%% got "+fields[i].getType() +"   for "+ fieldname +"  fields[i].getReverse(): "+fields[i].getReverse());
block|}
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Object
name|objA
parameter_list|,
name|Object
name|objB
parameter_list|)
block|{
name|ShardDoc
name|docA
init|=
operator|(
name|ShardDoc
operator|)
name|objA
decl_stmt|;
name|ShardDoc
name|docB
init|=
operator|(
name|ShardDoc
operator|)
name|objB
decl_stmt|;
comment|// If these docs are from the same shard, then the relative order
comment|// is how they appeared in the response from that shard.
if|if
condition|(
name|docA
operator|.
name|shard
operator|==
name|docB
operator|.
name|shard
condition|)
block|{
comment|// if docA has a smaller position, it should be "larger" so it
comment|// comes before docB.
comment|// This will handle sorting by docid within the same shard
comment|// comment this out to test comparators.
return|return
operator|!
operator|(
name|docA
operator|.
name|orderInShard
operator|<
name|docB
operator|.
name|orderInShard
operator|)
return|;
block|}
comment|// run comparators
specifier|final
name|int
name|n
init|=
name|comparators
operator|.
name|length
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
operator|&&
name|c
operator|==
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
operator|(
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
operator|)
condition|?
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docB
argument_list|,
name|docA
argument_list|)
else|:
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docA
argument_list|,
name|docB
argument_list|)
expr_stmt|;
block|}
comment|// solve tiebreaks by comparing shards (similar to using docid)
comment|// smaller docid's beat larger ids, so reverse the natural ordering
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
operator|-
name|docA
operator|.
name|shard
operator|.
name|compareTo
argument_list|(
name|docB
operator|.
name|shard
argument_list|)
expr_stmt|;
block|}
return|return
name|c
operator|<
literal|0
return|;
block|}
DECL|method|getCachedComparator
name|Comparator
name|getCachedComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|type
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|FieldComparatorSource
name|factory
parameter_list|)
block|{
name|Comparator
name|comparator
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SortField
operator|.
name|SCORE
case|:
name|comparator
operator|=
name|comparatorScore
argument_list|(
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|STRING
case|:
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
name|comparator
operator|=
name|comparatorStringLocale
argument_list|(
name|fieldname
argument_list|,
name|locale
argument_list|)
expr_stmt|;
else|else
name|comparator
operator|=
name|comparatorNatural
argument_list|(
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|CUSTOM
case|:
if|if
condition|(
name|factory
operator|instanceof
name|MissingStringLastComparatorSource
condition|)
block|{
name|comparator
operator|=
name|comparatorMissingStringLast
argument_list|(
name|fieldname
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: support other types such as random... is there a way to
comment|// support generically?  Perhaps just comparing Object
name|comparator
operator|=
name|comparatorNatural
argument_list|(
name|fieldname
argument_list|)
expr_stmt|;
comment|// throw new RuntimeException("Custom sort not supported factory is "+factory.getClass());
block|}
break|break;
case|case
name|SortField
operator|.
name|DOC
case|:
comment|// TODO: we can support this!
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Doc sort not supported"
argument_list|)
throw|;
default|default:
name|comparator
operator|=
name|comparatorNatural
argument_list|(
name|fieldname
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|comparator
return|;
block|}
DECL|class|ShardComparator
class|class
name|ShardComparator
implements|implements
name|Comparator
block|{
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|fieldNum
name|int
name|fieldNum
decl_stmt|;
DECL|method|ShardComparator
specifier|public
name|ShardComparator
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|fieldNum
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldNames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fieldNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|this
operator|.
name|fieldNum
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|sortVal
name|Object
name|sortVal
parameter_list|(
name|ShardDoc
name|shardDoc
parameter_list|)
block|{
assert|assert
operator|(
name|shardDoc
operator|.
name|sortFieldValues
operator|.
name|getName
argument_list|(
name|fieldNum
argument_list|)
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|)
assert|;
name|List
name|lst
init|=
operator|(
name|List
operator|)
name|shardDoc
operator|.
name|sortFieldValues
operator|.
name|getVal
argument_list|(
name|fieldNum
argument_list|)
decl_stmt|;
return|return
name|lst
operator|.
name|get
argument_list|(
name|shardDoc
operator|.
name|orderInShard
argument_list|)
return|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|comparatorScore
specifier|static
name|Comparator
name|comparatorScore
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|Object
name|o1
parameter_list|,
specifier|final
name|Object
name|o2
parameter_list|)
block|{
name|ShardDoc
name|e1
init|=
operator|(
name|ShardDoc
operator|)
name|o1
decl_stmt|;
name|ShardDoc
name|e2
init|=
operator|(
name|ShardDoc
operator|)
name|o2
decl_stmt|;
specifier|final
name|float
name|f1
init|=
name|e1
operator|.
name|score
decl_stmt|;
specifier|final
name|float
name|f2
init|=
name|e2
operator|.
name|score
decl_stmt|;
if|if
condition|(
name|f1
operator|<
name|f2
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|f1
operator|>
name|f2
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
comment|// The lucene natural sort ordering corresponds to numeric
comment|// and string natural sort orderings (ascending).  Since
comment|// the PriorityQueue keeps the biggest elements by default,
comment|// we need to reverse the natural compare ordering so that the
comment|// smallest elements are kept instead of the largest... hence
comment|// the negative sign on the final compareTo().
DECL|method|comparatorNatural
name|Comparator
name|comparatorNatural
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|ShardComparator
argument_list|(
name|fieldName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|Object
name|o1
parameter_list|,
specifier|final
name|Object
name|o2
parameter_list|)
block|{
name|ShardDoc
name|sd1
init|=
operator|(
name|ShardDoc
operator|)
name|o1
decl_stmt|;
name|ShardDoc
name|sd2
init|=
operator|(
name|ShardDoc
operator|)
name|o2
decl_stmt|;
name|Comparable
name|v1
init|=
operator|(
name|Comparable
operator|)
name|sortVal
argument_list|(
name|sd1
argument_list|)
decl_stmt|;
name|Comparable
name|v2
init|=
operator|(
name|Comparable
operator|)
name|sortVal
argument_list|(
name|sd2
argument_list|)
decl_stmt|;
if|if
condition|(
name|v1
operator|==
name|v2
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|v1
operator|==
literal|null
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|v2
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
return|return
operator|-
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|comparatorStringLocale
name|Comparator
name|comparatorStringLocale
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
specifier|final
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
decl_stmt|;
return|return
operator|new
name|ShardComparator
argument_list|(
name|fieldName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|Object
name|o1
parameter_list|,
specifier|final
name|Object
name|o2
parameter_list|)
block|{
name|ShardDoc
name|sd1
init|=
operator|(
name|ShardDoc
operator|)
name|o1
decl_stmt|;
name|ShardDoc
name|sd2
init|=
operator|(
name|ShardDoc
operator|)
name|o2
decl_stmt|;
name|Comparable
name|v1
init|=
operator|(
name|Comparable
operator|)
name|sortVal
argument_list|(
name|sd1
argument_list|)
decl_stmt|;
name|Comparable
name|v2
init|=
operator|(
name|Comparable
operator|)
name|sortVal
argument_list|(
name|sd2
argument_list|)
decl_stmt|;
if|if
condition|(
name|v1
operator|==
name|v2
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|v1
operator|==
literal|null
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|v2
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
return|return
operator|-
name|collator
operator|.
name|compare
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|comparatorMissingStringLast
name|Comparator
name|comparatorMissingStringLast
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|ShardComparator
argument_list|(
name|fieldName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|Object
name|o1
parameter_list|,
specifier|final
name|Object
name|o2
parameter_list|)
block|{
name|ShardDoc
name|sd1
init|=
operator|(
name|ShardDoc
operator|)
name|o1
decl_stmt|;
name|ShardDoc
name|sd2
init|=
operator|(
name|ShardDoc
operator|)
name|o2
decl_stmt|;
name|Comparable
name|v1
init|=
operator|(
name|Comparable
operator|)
name|sortVal
argument_list|(
name|sd1
argument_list|)
decl_stmt|;
name|Comparable
name|v2
init|=
operator|(
name|Comparable
operator|)
name|sortVal
argument_list|(
name|sd2
argument_list|)
decl_stmt|;
if|if
condition|(
name|v1
operator|==
name|v2
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|v1
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|v2
operator|==
literal|null
condition|)
return|return
literal|1
return|;
return|return
operator|-
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
