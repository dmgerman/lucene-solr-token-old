begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package
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
name|Collection
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
name|IndexFileNames
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
name|values
operator|.
name|PackedIntsImpl
operator|.
name|IntsReader
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
name|values
operator|.
name|PackedIntsImpl
operator|.
name|IntsWriter
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
name|store
operator|.
name|Directory
import|;
end_import
begin_comment
comment|//nocommit - add mmap version
end_comment
begin_comment
comment|//nocommti - add bulk copy where possible
end_comment
begin_class
DECL|class|Ints
specifier|public
class|class
name|Ints
block|{
DECL|method|Ints
specifier|private
name|Ints
parameter_list|()
block|{   }
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|String
name|id
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|CSF_DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getWriter
specifier|public
specifier|static
name|Writer
name|getWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|useFixedArray
parameter_list|)
throws|throws
name|IOException
block|{
comment|//nocommit - implement fixed?!
return|return
operator|new
name|IntsWriter
argument_list|(
name|dir
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|getReader
specifier|public
specifier|static
name|Reader
name|getReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|useFixedArray
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IntsReader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
end_class
end_unit
