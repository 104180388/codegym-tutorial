package com.example.case_study_2.service;

import com.example.case_study_2.dto.AiDiagnoseRequestDto;
import com.example.case_study_2.dto.AiDiagnoseResponseDto;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MedicalExpertEngine {

    public AiDiagnoseResponseDto diagnose(AiDiagnoseRequestDto request) {
        String symptoms = request.getSymptoms() != null ? request.getSymptoms().toLowerCase(Locale.ROOT) : "";
        String serviceName = request.getServiceName() != null ? request.getServiceName().toLowerCase(Locale.ROOT) : "";
        String patientName = request.getPatientName() != null ? request.getPatientName() : "Bệnh nhân";
        String gender = request.getGender() != null ? request.getGender() : "MALE";
        Integer age = request.getAge() != null ? request.getAge() : 30;

        String diagnosis;
        String treatmentPlan;
        String prescriptionAdvice;
        String doctorNotes;

        // 1. SỐT XUẤT HUYẾT / SỐT SIÊU VI
        if ((symptoms.contains("sốt cao") || symptoms.contains("sốt 39") || symptoms.contains("sốt liên tục"))
                && (symptoms.contains("đau mình") || symptoms.contains("đau khớp") || symptoms.contains("đau mỏi") || symptoms.contains("hốc mắt") || symptoms.contains("phát ban"))) {
            diagnosis = "Theo dõi Sốt xuất huyết Dengue (ngày 2-3) / Sốt siêu vi cấp tính";
            treatmentPlan = "- Theo dõi sát nhiệt độ mỗi 4 giờ và dấu hiệu xuất huyết dưới da / niêm mạc.\n" +
                    "- Chỉ định xét nghiệm: Tổng phân tích tế bào máu (CBC - theo dõi Bạch cầu, Tiểu cầu, Hct), Test nhanh Dengue NS1 Ag.\n" +
                    "- Uống nhiều nước (Oresol pha đúng tỷ lệ, nước dừa, nước hoa quả tươi 2.0 - 2.5 lít/ngày).\n" +
                    "- Nghỉ ngơi tuyệt đối tại giường, ăn thức ăn lỏng mềm, dễ tiêu.";
            prescriptionAdvice = "1. Paracetamol 500mg (Hạ sốt - giảm đau): Uống 1 viên khi sốt trên 38.5°C, mỗi lần cách nhau 4 - 6 giờ (Không dùng quá 4 viên/24h). Tuyệt đối không dùng Aspirin hoặc Ibuprofen.\n" +
                    "2. Oresol 245 (Bù nước & điện giải): Pha 1 gói trong đúng 200ml nước đun sôi để nguội, uống rải rác trong ngày (1.5 - 2 lít/ngày).\n" +
                    "3. Vitamin C 500mg: Uống 1 viên sau ăn sáng (Tăng sức đề kháng, bền thành mạch).";
            doctorNotes = "Tái khám ngay lập tức nếu xuất hiện dấu hiệu cảnh báo: Đau bụng nhiều, nôn liên tục, chảy máu chân răng/chảy máu cam, nôn ra máu, đi ngoài phân đen, mệt lả li bì hoặc chân tay lạnh ẩm. Hẹn tái khám kiểm tra công thức máu sau 24 - 48 giờ.";
        }
        // 2. VIÊM HỌNG CẤP / VIÊM ĐƯỜNG HÔ HẤP TRÊN / VIÊM AMIDAN
        else if (symptoms.contains("họng") || symptoms.contains("nuốt đau") || (symptoms.contains("ho") && (symptoms.contains("rát cổ") || symptoms.contains("sốt")))) {
            diagnosis = "Viêm họng cấp - Viêm đường hô hấp trên / Theo dõi bội nhiễm vi khuẩn nhẹ";
            treatmentPlan = "- Vệ sinh mũi họng hàng ngày bằng dung dịch nước muối sinh lý NaCl 0.9% (3-4 lần/ngày).\n" +
                    "- Giữ ấm vùng cổ họng, tránh uống nước đá lạnh, bia rượu hoặc tiếp xúc khói bụi.\n" +
                    "- Nghỉ ngơi, bổ sung dinh dưỡng giàu vitamin C, uống nhiều nước ấm.";
            prescriptionAdvice = "1. Cefuroxim 500mg (Kháng sinh): Uống 1 viên x 2 lần/ngày (sau ăn sáng, tối), dùng liên tục 5-7 ngày.\n" +
                    "2. Paracetamol 500mg: Uống 1 viên khi sốt trên 38.5°C hoặc đau rát họng nhiều (cách nhau tối thiểu 4-6h).\n" +
                    "3. Alpha Chymotrypsin (Chống phù nề kháng viêm): Ngậm dưới lưỡi 2 viên x 2-3 lần/ngày.\n" +
                    "4. Siro Ho / Acetylcystein 200mg (Long đờm): Uống 1 gói x 2-3 lần/ngày pha với nước ấm.\n" +
                    "5. Nước súc họng Povidone-Iodine 1% hoặc NaCl 0.9%: Súc miệng họng 3-4 lần/ngày.";
            doctorNotes = "Dặn dò bệnh nhân uống thuốc đúng liều lượng và đủ ngày kháng sinh, không tự ý ngừng thuốc sớm. Tái khám sau 5 ngày hoặc đến khám lại ngay nếu sốt cao liên tục không hạ, khó thở hoặc khàn tiếng nặng.";
        }
        // 3. VIÊM MŨI DỊ ỨNG / VIÊM XOANG
        else if (symptoms.contains("sổ mũi") || symptoms.contains("nghẹt mũi") || symptoms.contains("chảy nước mũi") || symptoms.contains("hắt hơi") || symptoms.contains("xoang")) {
            diagnosis = "Viêm mũi dị ứng cấp đợt bùng phát / Viêm mũi xoang xuất tiết";
            treatmentPlan = "- Hạn chế tối đa tiếp xúc tác nhân gây dị ứng: bụi nhà, phấn hoa, lông thú, máy lạnh nhiệt độ quá thấp.\n" +
                    "- Rửa mũi bằng bình xịt nước muối biển sâu 2-3 lần/ngày trước khi dùng thuốc xịt.\n" +
                    "- Đeo khẩu trang khi ra đường hoặc dọn dẹp nhà cửa.";
            prescriptionAdvice = "1. Fexofenadine 180mg (Kháng Histamin H1): Uống 1 viên/ngày vào buổi sáng hoặc tối sau ăn.\n" +
                    "2. Thuốc xịt mũi Budesonide / Fluticasone: Xịt mỗi bên mũi 1 nhát x 2 lần/ngày trong 7 ngày.\n" +
                    "3. Nước muối sinh lý xịt mũi (Xisat/Physiodose): Xịt rửa mũi 3-4 lần/ngày.\n" +
                    "4. Paracetamol 500mg: Uống 1 viên khi có đau tức vùng trán / cánh mũi (khi đau).";
            doctorNotes = "Tránh nằm điều hòa dưới 26 độ C và tránh gió thổi thẳng vào mặt. Tái khám sau 7 ngày nếu các triệu chứng nghẹt mũi, chảy dịch mũi vàng xanh đặc hoặc đau nhức hốc mắt không cải thiện.";
        }
        // 4. VIÊM DẠ DÀY - TÁ TRÀNG / TRÀO NGƯỢC DẠ DÀY THỰC QUẢN (GERD)
        else if (symptoms.contains("dạ dày") || symptoms.contains("thượng vị") || symptoms.contains("ợ chua") || symptoms.contains("ợ hơi") || symptoms.contains("nóng rát") || symptoms.contains("trào ngược")) {
            diagnosis = "Viêm dạ dày tá tràng cấp - Trào ngược dạ dày thực quản (GERD độ A-B)";
            treatmentPlan = "- Ăn uống đúng giờ, không bỏ bữa, không ăn quá no hoặc để bụng quá đói.\n" +
                    "- Kiêng đồ chua, cay nóng, dầu mỡ, cà phê, trà đặc, nước ngọt có ga và bia rượu.\n" +
                    "- Không nằm ngay sau khi ăn (chờ tối thiểu 2-3 giờ), nâng cao đầu giường 15 độ khi ngủ.";
            prescriptionAdvice = "1. Esomeprazole 40mg (Ức chế tiết acid PPI): Uống 1 viên trước bữa ăn sáng 30 - 60 phút, dùng liên tục 14 ngày.\n" +
                    "2. Phosphalugel (Gel nhôm phosphat - Trung hòa acid): Uống 1 gói khi đau rát hoặc sau ăn 1-2 giờ (2-3 gói/ngày).\n" +
                    "3. Domperidon 10mg / Itopride 50mg (Điều hòa nhu động ruột): Uống 1 viên trước ăn 15 phút x 2 lần/ngày (sáng, tối).\n" +
                    "4. Men vi sinh hỗ trợ tiêu hóa (Bioflora/Enterogermina): Uống 1-2 ống/ngày sau ăn.";
            doctorNotes = "Kiêng khem nghiêm ngặt chế độ ăn uống và sinh hoạt, tránh căng thẳng thức khuya. Tái khám sau 2 tuần hoặc nội soi dạ dày kiểm tra nếu đau bụng dữ dội, nôn ra máu, đi ngoài phân đen hoặc sụt cân không rõ nguyên nhân.";
        }
        // 5. TIÊU CHẢY CẤP / RỐI LOẠN TIÊU HÓA / VIÊM DẠ DÀY RUỘT
        else if (symptoms.contains("tiêu chảy") || symptoms.contains("đi ngoài") || symptoms.contains("nôn") || symptoms.contains("đau bụng") || symptoms.contains("ngộ độc")) {
            diagnosis = "Rối loạn tiêu hóa cấp tính / Theo dõi Viêm dạ dày ruột cấp do ăn uống";
            treatmentPlan = "- Bù nước và điện giải tích cực là ưu tiên hàng đầu, tránh mất nước.\n" +
                    "- Ăn cháo loãng nấu với thịt nạc, cà rốt, chuối chín, súp nhẹ; tránh dầu mỡ, sữa tươi và đồ sống lạnh.\n" +
                    "- Rửa tay xà phòng trước khi ăn và sau khi đi vệ sinh.";
            prescriptionAdvice = "1. Oresol 245: Pha 1 gói với 200ml nước sôi để nguội, uống 100-200ml sau mỗi lần đi ngoài phân lỏng.\n" +
                    "2. Smecta (Diosmectite 3g - Bảo vệ niêm mạc ruột): Uống 1 gói x 3 lần/ngày, hòa tan vào nửa ly nước ấm, uống xa bữa ăn.\n" +
                    "3. Men vi sinh sống Enterogermina (Bacillus clausii): Uống 1 ống x 2 lần/ngày sau bữa ăn.\n" +
                    "4. Drotaverin 40mg (Nospa - Giảm co thắt cơ trơn đường tiêu hóa): Uống 1 viên khi đau quặn bụng (tối đa 3 viên/ngày).";
            doctorNotes = "Theo dõi số lần đi ngoài và màu sắc phân. Đến ngay cơ sở y tế nếu có dấu hiệu: Đi ngoài phân có máu/mủ, sốt cao trên 38.5 độ C, nôn liên tục không uống được nước, hoặc có biểu hiện mất nước (khô môi, mắt trũng, khát nhiều, mệt lả).";
        }
        // 6. RỐI LOẠN TIỀN ĐÌNH / CHÓNG MẶT / THIẾU MÁU NÃO
        else if (symptoms.contains("chóng mặt") || symptoms.contains("tiền đình") || symptoms.contains("hoa mắt") || symptoms.contains("choáng váng") || symptoms.contains("mất thăng bằng")) {
            diagnosis = "Hội chứng tiền đình ngoại biên / Thiểu năng tuần hoàn não cấp tính";
            treatmentPlan = "- Tránh thay đổi tư thế đột ngột (đặc biệt khi ngồi dậy hoặc quay đầu nhanh).\n" +
                    "- Nghỉ ngơi nơi phòng yên tĩnh, ánh sáng dịu, kê gối vừa phải.\n" +
                    "- Tránh lái xe, làm việc trên cao hoặc vận hành máy móc nguy hiểm khi còn triệu chứng chóng mặt.";
            prescriptionAdvice = "1. Betahistine dihydrochloride 16mg (Tanganil / Betaserc): Uống 1 viên x 2 lần/ngày sau bữa ăn.\n" +
                    "2. Ginkgo Biloba 120mg (Hoạt huyết dưỡng não): Uống 1 viên x 2 lần/ngày vào buổi sáng và trưa.\n" +
                    "3. Piracetam 800mg: Uống 1 viên x 2 lần/ngày sau ăn.\n" +
                    "4. Magne-B6: Uống 1 viên x 2 lần/ngày sau ăn (hỗ trợ dẫn truyền thần kinh, giảm căng thẳng).";
            doctorNotes = "Dặn bệnh nhân sinh hoạt nhẹ nhàng, ngủ đủ giấc 7-8 tiếng/ngày, tránh căng thẳng thần kinh. Tái khám sau 7-10 ngày hoặc đi khám chuyên khoa Thần kinh nếu chóng mặt kèm tê yếu nửa người, nói khó, nhìn đôi.";
        }
        // 7. TĂNG HUYẾT ÁP / TIM MẠCH
        else if (symptoms.contains("huyết áp") || symptoms.contains("đau đầu vùng chẩm") || symptoms.contains("tim đập nhanh") || symptoms.contains("hồi hộp")) {
            diagnosis = "Tăng huyết áp nguyên phát độ 1-2 / Theo dõi rối loạn thần kinh tim";
            treatmentPlan = "- Theo dõi và ghi nhật ký huyết áp 2 lần/ngày (sáng sau ngủ dậy và tối trước đi ngủ).\n" +
                    "- Chế độ ăn giảm muối (< 5g muối/ngày), hạn chế mỡ động vật, tăng cường rau xanh, ngũ cốc nguyên hạt.\n" +
                    "- Tập thể dục nhẹ nhàng 30 phút mỗi ngày (đi bộ, đạp xe), bỏ thuốc lá và hạn chế bia rượu tuyệt đối.";
            prescriptionAdvice = "1. Amlodipine 5mg (Chẹn kênh calci): Uống 1 viên vào một giờ cố định buổi sáng hàng ngày.\n" +
                    "2. Losartan 50mg (Ức chế thụ thể Angiotensin II): Uống 1 viên buổi sáng theo chỉ định nếu cần phối hợp.\n" +
                    "3. Seduxen / Thảo dược dưỡng tâm an thần: Uống buổi tối nếu mất ngủ hoặc lo âu nhiều.";
            doctorNotes = "Uống thuốc huyết áp đều đặn mỗi ngày, không được tự ý bỏ thuốc ngay cả khi huyết áp đã về mức bình thường. Tái khám sau 14 ngày mang theo sổ theo dõi huyết áp hoặc đi khám cấp cứu nếu HA >= 180/110 mmHg kèm đau ngực, khó thở, nhức đầu dữ dội.";
        }
        // 8. ĐAU LƯNG / ĐAU VAI GÁY / CƠ XƯƠNG KHỚP
        else if (symptoms.contains("đau lưng") || symptoms.contains("vai gáy") || symptoms.contains("cột sống") || symptoms.contains("thoái hóa") || symptoms.contains("khớp")) {
            diagnosis = "Hội chứng đau cột sống thắt lưng cấp / Thoái hóa cột sống - Căng cơ cạnh sống";
            treatmentPlan = "- Nghỉ ngơi trên đệm cứng, tránh nằm võng hoặc đệm quá mềm lún.\n" +
                    "- Tránh mang vác vật nặng, tránh cúi gập người đột ngột hoặc ngồi sai tư thế lâu quá 1 tiếng liên tục.\n" +
                    "- Chườm ấm vùng đau 15-20 phút/lần x 2 lần/ngày, kết hợp vật lý trị liệu nhẹ nhàng.";
            prescriptionAdvice = "1. Meloxicam 7.5mg (Kháng viêm không steroid - NSAIDs): Uống 1 viên x 2 lần/ngày sau khi ăn no.\n" +
                    "2. Eperisone HCl 50mg (Myonal - Giãn cơ vân): Uống 1 viên x 2-3 lần/ngày sau bữa ăn.\n" +
                    "3. Paracetamol 500mg: Uống 1 viên khi đau nhiều (tối đa 3-4 lần/ngày).\n" +
                    "4. Glucosamine Sulfate 500mg: Uống 1 viên x 2 lần/ngày sau ăn (dùng dài hạn hỗ trợ khớp).";
            doctorNotes = "Dặn bệnh nhân không tự ý vặn bẻ khớp cổ/lưng quá mức. Tập các bài tập kéo giãn cơ lưng, vai gáy sau khi đỡ đau cấp. Tái khám sau 10 ngày hoặc chụp MRI cột sống nếu đau lan tê xuống chân/tay hoặc yếu cơ.";
        }
        // 9. DỊ ỨNG / MỀ ĐAY / VIÊM DA
        else if (symptoms.contains("dị ứng") || symptoms.contains("ngứa") || symptoms.contains("mề đay") || symptoms.contains("nổi mẩn") || symptoms.contains("da")) {
            diagnosis = "Viêm da tiếp xúc dị ứng / Mề đay dị ứng cấp tính";
            treatmentPlan = "- Tìm và loại trừ các tác nhân gây kích ứng (hóa mỹ phẩm mới, xà phòng tẩy rửa mạnh, hải sản, phấn hoa, lông động vật).\n" +
                    "- Không gãi, cào xước làm tổn thương và bội nhiễm vùng da viêm.\n" +
                    "- Vệ sinh da nhẹ nhàng bằng nước ấm, mặc quần áo rộng rãi thoáng mát bằng chất liệu cotton mềm.";
            prescriptionAdvice = "1. Levocetirizine 5mg / Loratadine 10mg (Kháng dị ứng): Uống 1 viên vào buổi tối sau ăn.\n" +
                    "2. Kem bôi ngoài da Hydrocortisone 1% hoặc Fucicort (nếu có dấu hiệu nhiễm trùng nhẹ): Bôi một lớp mỏng lên vùng da tổn thương 1-2 lần/ngày trong tối đa 5-7 ngày.\n" +
                    "3. Vitamin C 500mg + Kẽm Gluconat: Uống 1 viên/ngày sau ăn sáng giúp phục hồi tái tạo da.";
            doctorNotes = "Không tự ý mua các loại thuốc bôi corticoid nồng độ cao dùng kéo dài. Tái khám sau 7 ngày hoặc đến bệnh viện ngay nếu xuất hiện sưng phù mí mắt, phù môi, khó thở hoặc khò khè (phù mạch/phản vệ).";
        }
        // 10. MẮT ĐỎ / VIÊM KẾT MẠC
        else if (symptoms.contains("đau mắt") || symptoms.contains("đỏ mắt") || symptoms.contains("chảy nước mắt") || symptoms.contains("ghèn mắt")) {
            diagnosis = "Viêm kết mạc cấp (Đau mắt đỏ) / Kích ứng mắt do khói bụi vi khuẩn";
            treatmentPlan = "- Không dụi mắt bằng tay, dùng khăn mặt riêng và giặt phơi nắng hàng ngày.\n" +
                    "- Rửa mắt bằng nước muối sinh lý NaCl 0.9% trước khi nhỏ thuốc điều trị.\n" +
                    "- Đeo kính râm bảo vệ mắt khi đi ra ngoài trời.";
            prescriptionAdvice = "1. Nước mắt nhân tạo / Nước muối sinh lý NaCl 0.9% chuyên dụng cho mắt: Nhỏ 1-2 giọt x 4-6 lần/ngày.\n" +
                    "2. Dung dịch nhỏ mắt kháng khuẩn Tobramycin 0.3% (Tobrex): Nhỏ 1-2 giọt vào mắt bị viêm x 3-4 lần/ngày trong 5-7 ngày.\n" +
                    "3. Paracetamol 500mg: Uống khi đau tức vùng quanh mắt hoặc nhức đầu.";
            doctorNotes = "Tái khám sau 5 ngày. Nếu mắt mờ, sợ ánh sáng dữ dội hoặc đau nhức sâu trong nhãn cầu cần khám ngay bác sĩ chuyên khoa Mắt.";
        }
        // 11. MẶC ĐỊNH TỔNG QUÁT THEO TRIỆU CHỨNG BÁC SĨ NHẬP
        else {
            String symptomText = !symptoms.isEmpty() ? request.getSymptoms() : "Ghi nhận mệt mỏi thể chất, rối loạn sức khỏe lâm sàng tổng quát";
            diagnosis = "Theo dõi hội chứng lâm sàng: " + symptomText + " / Thể trạng suy nhược nhẹ";
            treatmentPlan = "- Thực hiện điều chỉnh chế độ sinh hoạt, dinh dưỡng lành mạnh, uống đủ 2 lít nước/ngày.\n" +
                    "- Nghỉ ngơi hợp lý, ngủ đủ giấc, tránh làm việc căng thẳng quá sức.\n" +
                    "- Chỉ định cận lâm sàng: Tổng phân tích máu (CBC), sinh hóa máu cơ bản nếu triệu chứng kéo dài.";
            prescriptionAdvice = "1. Multivitamin tổng hợp + Khoáng chất (Pharmaton / Berocca): Uống 1 viên sau bữa ăn sáng.\n" +
                    "2. Paracetamol 500mg: Uống 1 viên khi có biểu hiện đau đầu, sốt hoặc mệt mỏi nhiều (tối đa 3 lần/ngày khi cần).\n" +
                    "3. Men vi sinh / Trà thảo mộc an thần: Hỗ trợ tiêu hóa và nâng cao chất lượng giấc ngủ.";
            doctorNotes = "Dặn dò người bệnh theo dõi các phản ứng của cơ thể trong 3-5 ngày tới. Hẹn tái khám sau 5-7 ngày hoặc khám lại ngay khi xuất hiện các triệu chứng bất thường khác.";
        }

        return AiDiagnoseResponseDto.success(diagnosis, treatmentPlan, prescriptionAdvice, doctorNotes, "AI-Medical-Expert");
    }
}
