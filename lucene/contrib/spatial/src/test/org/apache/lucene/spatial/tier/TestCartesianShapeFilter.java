begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
package|;
end_package
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
name|NotSerializableException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  *   * Test for {@link CartesianShapeFilter}  *  */
end_comment
begin_class
DECL|class|TestCartesianShapeFilter
specifier|public
class|class
name|TestCartesianShapeFilter
extends|extends
name|TestCase
block|{
DECL|method|testSerializable
specifier|public
name|void
name|testSerializable
parameter_list|()
throws|throws
name|IOException
block|{
name|CartesianShapeFilter
name|filter
init|=
operator|new
name|CartesianShapeFilter
argument_list|(
operator|new
name|Shape
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|oos
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|bos
argument_list|)
decl_stmt|;
name|oos
operator|.
name|writeObject
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotSerializableException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Filter should be serializable but raised a NotSerializableException ["
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
