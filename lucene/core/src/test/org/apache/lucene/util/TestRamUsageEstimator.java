begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomStrings
import|;
end_import
begin_class
DECL|class|TestRamUsageEstimator
specifier|public
class|class
name|TestRamUsageEstimator
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSanity
specifier|public
name|void
name|testSanity
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|sizeOf
argument_list|(
operator|new
name|String
argument_list|(
literal|"test string"
argument_list|)
argument_list|)
operator|>
name|shallowSizeOfInstance
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Holder
name|holder
init|=
operator|new
name|Holder
argument_list|()
decl_stmt|;
name|holder
operator|.
name|holder
operator|=
operator|new
name|Holder
argument_list|(
literal|"string2"
argument_list|,
literal|5000L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sizeOf
argument_list|(
name|holder
argument_list|)
operator|>
name|shallowSizeOfInstance
argument_list|(
name|Holder
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sizeOf
argument_list|(
name|holder
argument_list|)
operator|>
name|sizeOf
argument_list|(
name|holder
operator|.
name|holder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|shallowSizeOfInstance
argument_list|(
name|HolderSubclass
operator|.
name|class
argument_list|)
operator|>=
name|shallowSizeOfInstance
argument_list|(
name|Holder
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|shallowSizeOfInstance
argument_list|(
name|Holder
operator|.
name|class
argument_list|)
operator|==
name|shallowSizeOfInstance
argument_list|(
name|HolderSubclass2
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|strings
init|=
operator|new
name|String
index|[]
block|{
operator|new
name|String
argument_list|(
literal|"test string"
argument_list|)
block|,
operator|new
name|String
argument_list|(
literal|"hollow"
argument_list|)
block|,
operator|new
name|String
argument_list|(
literal|"catchmaster"
argument_list|)
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|sizeOf
argument_list|(
name|strings
argument_list|)
operator|>
name|shallowSizeOf
argument_list|(
name|strings
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStaticOverloads
specifier|public
name|void
name|testStaticOverloads
parameter_list|()
block|{
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
block|{
name|byte
index|[]
name|array
init|=
operator|new
name|byte
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|boolean
index|[]
name|array
init|=
operator|new
name|boolean
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|char
index|[]
name|array
init|=
operator|new
name|char
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|short
index|[]
name|array
init|=
operator|new
name|short
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|int
index|[]
name|array
init|=
operator|new
name|int
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|float
index|[]
name|array
init|=
operator|new
name|float
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|long
index|[]
name|array
init|=
operator|new
name|long
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|double
index|[]
name|array
init|=
operator|new
name|double
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|sizeOf
argument_list|(
name|array
argument_list|)
argument_list|,
name|sizeOf
argument_list|(
operator|(
name|Object
operator|)
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReferenceSize
specifier|public
name|void
name|testReferenceSize
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isSupportedJVM
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARN: Your JVM does not support certain Oracle/Sun extensions."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|" Memory estimates may be inaccurate."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|" Please report this to the Lucene mailing list."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"JVM version: "
operator|+
name|RamUsageEstimator
operator|.
name|JVM_INFO_STRING
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"UnsupportedFeatures:"
argument_list|)
expr_stmt|;
for|for
control|(
name|JvmFeature
name|f
range|:
name|RamUsageEstimator
operator|.
name|getUnsupportedFeatures
argument_list|()
control|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|" - "
operator|+
name|f
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|==
name|RamUsageEstimator
operator|.
name|JvmFeature
operator|.
name|OBJECT_ALIGNMENT
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"; Please note: 32bit Oracle/Sun VMs don't allow exact OBJECT_ALIGNMENT retrieval, this is a known issue."
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|NUM_BYTES_OBJECT_REF
operator|==
literal|4
operator|||
name|NUM_BYTES_OBJECT_REF
operator|==
literal|8
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Constants
operator|.
name|JRE_IS_64BIT
condition|)
block|{
name|assertEquals
argument_list|(
literal|"For 32bit JVMs, reference size must always be 4?"
argument_list|,
literal|4
argument_list|,
name|NUM_BYTES_OBJECT_REF
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|class|Holder
specifier|private
specifier|static
class|class
name|Holder
block|{
DECL|field|field1
name|long
name|field1
init|=
literal|5000L
decl_stmt|;
DECL|field|name
name|String
name|name
init|=
literal|"name"
decl_stmt|;
DECL|field|holder
name|Holder
name|holder
decl_stmt|;
DECL|field|field2
DECL|field|field3
DECL|field|field4
name|long
name|field2
decl_stmt|,
name|field3
decl_stmt|,
name|field4
decl_stmt|;
DECL|method|Holder
name|Holder
parameter_list|()
block|{}
DECL|method|Holder
name|Holder
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|field1
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|field1
operator|=
name|field1
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|class|HolderSubclass
specifier|private
specifier|static
class|class
name|HolderSubclass
extends|extends
name|Holder
block|{
DECL|field|foo
name|byte
name|foo
decl_stmt|;
DECL|field|bar
name|int
name|bar
decl_stmt|;
block|}
DECL|class|HolderSubclass2
specifier|private
specifier|static
class|class
name|HolderSubclass2
extends|extends
name|Holder
block|{
comment|// empty, only inherits all fields -> size should be identical to superclass
block|}
block|}
end_class
end_unit
