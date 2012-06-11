begin_unit
begin_package
DECL|package|org.apache.lucene.facet.index
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
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|ObjectInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|FacetException
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
name|enhancements
operator|.
name|association
operator|.
name|AssociationIntProperty
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
name|enhancements
operator|.
name|association
operator|.
name|AssociationProperty
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
name|CategoryContainer
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
name|attributes
operator|.
name|CategoryAttribute
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
name|attributes
operator|.
name|CategoryAttributeImpl
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
name|streaming
operator|.
name|CategoryAttributesStream
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|CategoryContainerTest
specifier|public
class|class
name|CategoryContainerTest
extends|extends
name|CategoryContainerTestBase
block|{
annotation|@
name|Test
DECL|method|basicTest
specifier|public
name|void
name|basicTest
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Wrong number of categories in the container"
argument_list|,
literal|3
argument_list|,
name|categoryContainer
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|categoryContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Container should not contain categories after clear"
argument_list|,
literal|0
argument_list|,
name|categoryContainer
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIterator
specifier|public
name|void
name|testIterator
parameter_list|()
throws|throws
name|FacetException
block|{
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
init|=
name|categoryContainer
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// count the number of tokens
name|int
name|nCategories
decl_stmt|;
for|for
control|(
name|nCategories
operator|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|nCategories
operator|++
control|)
block|{
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|3
argument_list|,
name|nCategories
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExistingNewCategoryWithProperty
specifier|public
name|void
name|testExistingNewCategoryWithProperty
parameter_list|()
throws|throws
name|FacetException
block|{
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"five"
argument_list|,
literal|"six"
argument_list|)
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
init|=
name|categoryContainer
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// count the number of tokens, and check there is one DummyAttribute
name|int
name|nCategories
decl_stmt|;
name|int
name|nProperties
init|=
literal|0
decl_stmt|;
for|for
control|(
name|nCategories
operator|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|nCategories
operator|++
control|)
block|{
name|CategoryAttribute
name|attribute
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|attribute
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nProperties
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|3
argument_list|,
name|nCategories
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with properties"
argument_list|,
literal|1
argument_list|,
name|nProperties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleCategoriesWithProperties
specifier|public
name|void
name|testMultipleCategoriesWithProperties
parameter_list|()
throws|throws
name|FacetException
block|{
name|AssociationProperty
name|associationProperty
init|=
operator|new
name|AssociationIntProperty
argument_list|(
literal|49
argument_list|)
decl_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"five"
argument_list|,
literal|"six"
argument_list|)
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|,
name|associationProperty
argument_list|)
expr_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"seven"
argument_list|,
literal|"eight"
argument_list|)
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|associationProperty
operator|=
operator|new
name|AssociationIntProperty
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"nine"
argument_list|)
argument_list|,
name|associationProperty
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
init|=
name|categoryContainer
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// count the number of tokens, and check there is one DummyAttribute
name|int
name|nCategories
decl_stmt|;
name|int
name|nDummyAttributes
init|=
literal|0
decl_stmt|;
name|int
name|nAssocAttributes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|nCategories
operator|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|nCategories
operator|++
control|)
block|{
name|CategoryAttribute
name|attribute
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|attribute
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nDummyAttributes
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|attribute
operator|.
name|getProperty
argument_list|(
name|AssociationIntProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nAssocAttributes
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|5
argument_list|,
name|nCategories
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with dummy properties"
argument_list|,
literal|3
argument_list|,
name|nDummyAttributes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with association properties"
argument_list|,
literal|2
argument_list|,
name|nAssocAttributes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddNewCategoryWithProperty
specifier|public
name|void
name|testAddNewCategoryWithProperty
parameter_list|()
throws|throws
name|FacetException
block|{
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"seven"
argument_list|,
literal|"eight"
argument_list|)
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
init|=
name|categoryContainer
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// count the number of tokens, and check there is one DummyAttribute
name|int
name|nCategories
decl_stmt|;
name|int
name|nProperties
init|=
literal|0
decl_stmt|;
for|for
control|(
name|nCategories
operator|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|nCategories
operator|++
control|)
block|{
name|CategoryAttribute
name|attribute
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|attribute
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nProperties
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|4
argument_list|,
name|nCategories
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with properties"
argument_list|,
literal|1
argument_list|,
name|nProperties
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test addition of {@link CategoryAttribute} object without properties to a    * {@link CategoryContainer}.    *     * @throws FacetException    */
annotation|@
name|Test
DECL|method|testAddCategoryAttributeWithoutProperties
specifier|public
name|void
name|testAddCategoryAttributeWithoutProperties
parameter_list|()
throws|throws
name|FacetException
block|{
name|CategoryAttribute
name|newCA
init|=
operator|new
name|CategoryAttributeImpl
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"seven"
argument_list|,
literal|"eight"
argument_list|)
argument_list|)
decl_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|newCA
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test addition of {@link CategoryAttribute} object with property to a    * {@link CategoryContainer}.    *     * @throws FacetException    */
annotation|@
name|Test
DECL|method|testAddCategoryAttributeWithProperty
specifier|public
name|void
name|testAddCategoryAttributeWithProperty
parameter_list|()
throws|throws
name|FacetException
block|{
name|CategoryAttribute
name|newCA
init|=
operator|new
name|CategoryAttributeImpl
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"seven"
argument_list|,
literal|"eight"
argument_list|)
argument_list|)
decl_stmt|;
name|newCA
operator|.
name|addProperty
argument_list|(
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|newCA
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
init|=
name|categoryContainer
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// count the number of tokens, and check there is one DummyAttribute
name|int
name|nCategories
decl_stmt|;
name|int
name|nProperties
init|=
literal|0
decl_stmt|;
for|for
control|(
name|nCategories
operator|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|nCategories
operator|++
control|)
block|{
name|CategoryAttribute
name|attribute
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|attribute
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nProperties
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|4
argument_list|,
name|nCategories
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with properties"
argument_list|,
literal|1
argument_list|,
name|nProperties
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verifies that a {@link CategoryAttributesStream} can be constructed from    * {@link CategoryContainer} and produce the correct number of tokens.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testCategoryAttributesStream
specifier|public
name|void
name|testCategoryAttributesStream
parameter_list|()
throws|throws
name|IOException
block|{
name|CategoryAttributesStream
name|stream
init|=
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
decl_stmt|;
comment|// count the number of tokens
name|int
name|nTokens
decl_stmt|;
for|for
control|(
name|nTokens
operator|=
literal|0
init|;
name|stream
operator|.
name|incrementToken
argument_list|()
condition|;
name|nTokens
operator|++
control|)
block|{     }
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|3
argument_list|,
name|nTokens
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that {@link CategoryContainer} merges properties.    *     * @throws FacetException    */
annotation|@
name|Test
DECL|method|testCategoryAttributeMerge
specifier|public
name|void
name|testCategoryAttributeMerge
parameter_list|()
throws|throws
name|FacetException
block|{
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|initialCatgeories
index|[
literal|0
index|]
argument_list|,
operator|new
name|AssociationIntProperty
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|initialCatgeories
index|[
literal|0
index|]
argument_list|,
operator|new
name|AssociationIntProperty
argument_list|(
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
init|=
name|categoryContainer
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|nCategories
decl_stmt|;
name|int
name|nAssociations
init|=
literal|0
decl_stmt|;
for|for
control|(
name|nCategories
operator|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|nCategories
operator|++
control|)
block|{
name|CategoryAttribute
name|ca
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|AssociationProperty
name|aa
init|=
operator|(
name|AssociationProperty
operator|)
name|ca
operator|.
name|getProperty
argument_list|(
name|AssociationIntProperty
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|aa
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong association value"
argument_list|,
literal|17
argument_list|,
name|aa
operator|.
name|getAssociation
argument_list|()
argument_list|)
expr_stmt|;
name|nAssociations
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|3
argument_list|,
name|nCategories
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with associations"
argument_list|,
literal|1
argument_list|,
name|nAssociations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|AssociationProperty
name|associationProperty
init|=
operator|new
name|AssociationIntProperty
argument_list|(
literal|49
argument_list|)
decl_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"five"
argument_list|,
literal|"six"
argument_list|)
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|,
name|associationProperty
argument_list|)
expr_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"seven"
argument_list|,
literal|"eight"
argument_list|)
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|associationProperty
operator|=
operator|new
name|AssociationIntProperty
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"nine"
argument_list|)
argument_list|,
name|associationProperty
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|ObjectOutputStream
name|out
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|categoryContainer
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|ObjectInputStream
name|in
init|=
operator|new
name|ObjectInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Original and deserialized CategoryContainer are different"
argument_list|,
name|categoryContainer
argument_list|,
name|in
operator|.
name|readObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
