const ImagePreviewModal = ({ src, onClose }) => {
  if (!src) return null;

  return (
    <div className="image-preview-overlay" onClick={onClose}>
      <img
        src={src}
        className="image-preview"
        onClick={(e) => e.stopPropagation()}
      />
      <button className="close-btn" onClick={onClose}>
        ✕
      </button>
    </div>
  );
};
export default ImagePreviewModal;