begin_unit
begin_package
DECL|package|org.apache.lucene.facet.index.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|params
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
name|facet
operator|.
name|index
operator|.
name|categorypolicy
operator|.
name|OrdinalPolicy
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
name|facet
operator|.
name|index
operator|.
name|categorypolicy
operator|.
name|PathPolicy
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
name|facet
operator|.
name|search
operator|.
name|DrillDown
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|facet
operator|.
name|util
operator|.
name|PartitionsUtils
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|DefaultFacetIndexingParamsTest
specifier|public
class|class
name|DefaultFacetIndexingParamsTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testDefaultSettings
specifier|public
name|void
name|testDefaultSettings
parameter_list|()
block|{
name|FacetIndexingParams
name|dfip
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Missing default category list"
argument_list|,
name|dfip
operator|.
name|getAllCategoryListParams
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all categories have the same CategoryListParams by default"
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected default category list term is $facets:$fulltree$"
argument_list|,
operator|new
name|Term
argument_list|(
literal|"$facets"
argument_list|,
literal|"$fulltree$"
argument_list|)
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|expectedDDText
init|=
literal|"a"
operator|+
name|dfip
operator|.
name|getFacetDelimChar
argument_list|()
operator|+
literal|"b"
decl_stmt|;
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong drill-down term"
argument_list|,
operator|new
name|Term
argument_list|(
literal|"$facets"
argument_list|,
name|expectedDDText
argument_list|)
argument_list|,
name|DrillDown
operator|.
name|term
argument_list|(
name|dfip
argument_list|,
name|cp
argument_list|)
argument_list|)
expr_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|20
index|]
decl_stmt|;
name|int
name|numchars
init|=
name|dfip
operator|.
name|drillDownTermText
argument_list|(
name|cp
argument_list|,
name|buf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"3 characters should be written"
argument_list|,
literal|3
argument_list|,
name|numchars
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong drill-down term text"
argument_list|,
name|expectedDDText
argument_list|,
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|numchars
argument_list|)
argument_list|)
expr_stmt|;
name|CategoryListParams
name|clParams
init|=
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"partition for all ordinals is the first"
argument_list|,
literal|"$fulltree$"
argument_list|,
name|PartitionsUtils
operator|.
name|partitionNameByOrdinal
argument_list|(
name|dfip
argument_list|,
name|clParams
argument_list|,
literal|250
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"for partition 0, the same name should be returned"
argument_list|,
literal|"$fulltree$"
argument_list|,
name|PartitionsUtils
operator|.
name|partitionName
argument_list|(
name|clParams
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"for any other, it's the concatenation of name + partition"
argument_list|,
literal|"$fulltree$1"
argument_list|,
name|PartitionsUtils
operator|.
name|partitionName
argument_list|(
name|clParams
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default partition number is always 0"
argument_list|,
literal|0
argument_list|,
name|PartitionsUtils
operator|.
name|partitionNumber
argument_list|(
name|dfip
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default partition size is unbounded"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|dfip
operator|.
name|getPartitionSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCategoryListParamsWithDefaultIndexingParams
specifier|public
name|void
name|testCategoryListParamsWithDefaultIndexingParams
parameter_list|()
block|{
name|CategoryListParams
name|clp
init|=
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"clp"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|dfip
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|(
name|clp
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected default category list term is "
operator|+
name|clp
operator|.
name|getTerm
argument_list|()
argument_list|,
name|clp
operator|.
name|getTerm
argument_list|()
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCategoryPolicies
specifier|public
name|void
name|testCategoryPolicies
parameter_list|()
block|{
name|FacetIndexingParams
name|dfip
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|()
decl_stmt|;
comment|// check path policy
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|()
decl_stmt|;
name|PathPolicy
name|pathPolicy
init|=
name|PathPolicy
operator|.
name|ALL_CATEGORIES
decl_stmt|;
name|assertEquals
argument_list|(
literal|"path policy does not match default for root"
argument_list|,
name|pathPolicy
operator|.
name|shouldAdd
argument_list|(
name|cp
argument_list|)
argument_list|,
name|dfip
operator|.
name|getPathPolicy
argument_list|()
operator|.
name|shouldAdd
argument_list|(
name|cp
argument_list|)
argument_list|)
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nComponents
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
index|[]
name|components
init|=
operator|new
name|String
index|[
name|nComponents
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|components
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|components
index|[
name|j
index|]
operator|=
operator|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|cp
operator|=
operator|new
name|CategoryPath
argument_list|(
name|components
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"path policy does not match default for "
operator|+
name|cp
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|,
name|pathPolicy
operator|.
name|shouldAdd
argument_list|(
name|cp
argument_list|)
argument_list|,
name|dfip
operator|.
name|getPathPolicy
argument_list|()
operator|.
name|shouldAdd
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check ordinal policy
name|OrdinalPolicy
name|ordinalPolicy
init|=
name|OrdinalPolicy
operator|.
name|ALL_PARENTS
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ordinal policy does not match default for root"
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|TaxonomyReader
operator|.
name|ROOT_ORDINAL
argument_list|)
argument_list|,
name|dfip
operator|.
name|getOrdinalPolicy
argument_list|()
operator|.
name|shouldAdd
argument_list|(
name|TaxonomyReader
operator|.
name|ROOT_ORDINAL
argument_list|)
argument_list|)
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ordinal policy does not match default for "
operator|+
name|ordinal
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|ordinal
argument_list|)
argument_list|,
name|dfip
operator|.
name|getOrdinalPolicy
argument_list|()
operator|.
name|shouldAdd
argument_list|(
name|ordinal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
