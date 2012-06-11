begin_unit
begin_package
DECL|package|org.apache.lucene.facet.index.attributes
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
name|attributes
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|util
operator|.
name|LuceneTestCase
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
name|index
operator|.
name|DummyProperty
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
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|CategoryAttributeImplTest
specifier|public
class|class
name|CategoryAttributeImplTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testCategoryPath
specifier|public
name|void
name|testCategoryPath
parameter_list|()
block|{
name|CategoryAttribute
name|ca
init|=
operator|new
name|CategoryAttributeImpl
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Category Path should be null"
argument_list|,
name|ca
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
expr_stmt|;
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
name|ca
operator|.
name|setCategoryPath
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong Category Path"
argument_list|,
name|cp
argument_list|,
name|ca
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
expr_stmt|;
name|ca
operator|.
name|setCategoryPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Category Path should be null"
argument_list|,
name|ca
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
expr_stmt|;
name|ca
operator|=
operator|new
name|CategoryAttributeImpl
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong Category Path"
argument_list|,
name|cp
argument_list|,
name|ca
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProperties
specifier|public
name|void
name|testProperties
parameter_list|()
throws|throws
name|FacetException
block|{
name|CategoryAttribute
name|ca
init|=
operator|new
name|CategoryAttributeImpl
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Attribute should be null"
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Attribute classes should be null"
argument_list|,
name|ca
operator|.
name|getPropertyClasses
argument_list|()
argument_list|)
expr_stmt|;
name|ca
operator|.
name|addProperty
argument_list|(
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"DummyProperty should be in properties"
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Attribute classes should contain 1 element"
argument_list|,
literal|1
argument_list|,
name|ca
operator|.
name|getPropertyClasses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|ca
operator|.
name|addProperty
argument_list|(
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|failed
condition|)
block|{
name|fail
argument_list|(
literal|"Two DummyAttributes added to the same CategoryAttribute"
argument_list|)
expr_stmt|;
block|}
name|ca
operator|.
name|clearProperties
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Attribute classes should be null"
argument_list|,
name|ca
operator|.
name|getPropertyClasses
argument_list|()
argument_list|)
expr_stmt|;
name|ca
operator|.
name|addProperty
argument_list|(
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"DummyProperty should be in properties"
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ca
operator|.
name|remove
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"DummyProperty should not be in properties"
argument_list|,
literal|null
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Attribute classes should be null"
argument_list|,
name|ca
operator|.
name|getPropertyClasses
argument_list|()
argument_list|)
expr_stmt|;
name|ca
operator|.
name|addProperty
argument_list|(
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
name|propertyClasses
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No property expected when no classes given"
argument_list|,
literal|null
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|propertyClasses
argument_list|)
argument_list|)
expr_stmt|;
name|propertyClasses
operator|.
name|add
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"DummyProperty should be in properties"
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|propertyClasses
argument_list|)
argument_list|)
expr_stmt|;
name|propertyClasses
operator|.
name|add
argument_list|(
name|OrdinalProperty
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"DummyProperty should be in properties"
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|propertyClasses
argument_list|)
argument_list|)
expr_stmt|;
name|propertyClasses
operator|.
name|clear
argument_list|()
expr_stmt|;
name|propertyClasses
operator|.
name|add
argument_list|(
name|OrdinalProperty
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No ordinal property expected"
argument_list|,
literal|null
argument_list|,
name|ca
operator|.
name|getProperty
argument_list|(
name|propertyClasses
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloneCopyToAndSet
specifier|public
name|void
name|testCloneCopyToAndSet
parameter_list|()
throws|throws
name|FacetException
block|{
name|CategoryAttributeImpl
name|ca1
init|=
operator|new
name|CategoryAttributeImpl
argument_list|()
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
name|ca1
operator|.
name|setCategoryPath
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|ca1
operator|.
name|addProperty
argument_list|(
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|CategoryAttribute
name|ca2
init|=
name|ca1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Error in cloning"
argument_list|,
name|ca1
argument_list|,
name|ca2
argument_list|)
expr_stmt|;
name|CategoryAttributeImpl
name|ca3
init|=
operator|new
name|CategoryAttributeImpl
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
literal|"Should not be the same"
argument_list|,
name|ca1
argument_list|,
name|ca3
argument_list|)
expr_stmt|;
name|ca1
operator|.
name|copyTo
argument_list|(
name|ca3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Error in cloning"
argument_list|,
name|ca1
argument_list|,
name|ca3
argument_list|)
expr_stmt|;
name|ca2
operator|.
name|setCategoryPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"Should not be the same"
argument_list|,
name|ca1
argument_list|,
name|ca2
argument_list|)
expr_stmt|;
name|ca2
operator|.
name|set
argument_list|(
name|ca3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Error in cloning"
argument_list|,
name|ca1
argument_list|,
name|ca2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
